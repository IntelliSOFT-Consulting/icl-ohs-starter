/*
 * Copyright 2026 Open Health Stack Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package icl.ohs.libs.auth.network

import icl.ohs.libs.auth.model.AuthSession
import icl.ohs.libs.auth.model.LoginTokenResponse
import icl.ohs.libs.auth.model.ProviderLocationInfo
import icl.ohs.libs.auth.model.ProviderProfile
import icl.ohs.libs.auth.model.ProviderUser
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** JSON decoding of auth API responses into domain models, and failure-message resolution. */
internal fun parseLoginTokenResponse(responseBody: String): LoginTokenResponse? {
  val json = parseJsonObject(responseBody) ?: return null

  return LoginTokenResponse(
    accessToken = json.stringValue("access_token"),
    expiresIn = json.longValue("expires_in"),
    refreshExpiresIn = json.longValue("refresh_expires_in"),
    refreshToken = json.stringValue("refresh_token"),
    tokenType = json.stringValue("token_type"),
    notBeforePolicy = json.longValue("not-before-policy"),
    sessionState = json.stringValue("session_state"),
    scope = json.stringValue("scope"),
    firstLogin = json.booleanValue("firstLogin"),
    status = json.stringValue("status"),
  )
}

internal fun parseProviderProfile(responseBody: String): ProviderProfile? {
  val json = parseJsonObject(responseBody) ?: return null

  return ProviderProfile(
    status = json.rawStringValue("status"),
    user = json.objectValue("user")?.toProviderUser(),
  )
}

/**
 * Encodes a [ProviderProfile] back to the same JSON shape [parseProviderProfile] reads, so it can
 * be handed to [icl.ohs.libs.auth.model.AuthSessionStore.providerProfileJson] for persistence and
 * restored later with [parseProviderProfile] - keeping the two functions symmetric instead of
 * introducing a second, `@Serializable`-based encoding of the same models.
 */
internal fun ProviderProfile.toJsonString(): String =
  buildJsonObject {
      status?.let { put("status", it) }
      user?.let { put("user", it.toJsonObject()) }
    }
    .toString()

internal fun ProviderUser.toJsonObject(): JsonObject = buildJsonObject {
  firstName?.let { put("firstName", it) }
  lastName?.let { put("lastName", it) }
  fhirPractitionerId?.let { put("fhirPractitionerId", it) }
  practitionerRole?.let { put("practitionerRole", it) }
  role?.let { put("role", it) }
  status?.let { put("status", it) }
  id?.let { put("id", it) }
  idNumber?.let { put("idNumber", it) }
  fullNames?.let { put("fullNames", it) }
  phone?.let { put("phone", it) }
  email?.let { put("email", it) }
  locationInfo?.let { put("locationInfo", it.toJsonObject()) }
  communityHealthUnits?.let { units ->
    put("communityHealthUnits", JsonArray(units.map { JsonPrimitive(it) }))
  }
}

internal fun ProviderLocationInfo.toJsonObject(): JsonObject = buildJsonObject {
  facility?.let { put("facility", it) }
  facilityName?.let { put("facilityName", it) }
  ward?.let { put("ward", it) }
  wardName?.let { put("wardName", it) }
  subCounty?.let { put("subCounty", it) }
  subCountyName?.let { put("subCountyName", it) }
  county?.let { put("county", it) }
  countyName?.let { put("countyName", it) }
  country?.let { put("country", it) }
  countryName?.let { put("countryName", it) }
}

internal fun LoginTokenResponse.toAuthSession(issuedAt: Instant): AuthSession? {
  val accessToken = accessToken?.trim().orEmpty()
  val tokenType = tokenType?.trim().orEmpty()
  if (accessToken.isBlank() || tokenType.isBlank()) {
    return null
  }

  return AuthSession(
    accessToken = accessToken,
    tokenType = tokenType,
    refreshToken = refreshToken,
    issuedAt = issuedAt,
    accessTokenExpiresAt = expiresIn?.let { issuedAt + it.seconds },
    refreshTokenExpiresAt = refreshExpiresIn?.let { issuedAt + it.seconds },
    expiresInSeconds = expiresIn,
    refreshExpiresInSeconds = refreshExpiresIn,
    notBeforePolicy = notBeforePolicy,
    sessionState = sessionState,
    scope = scope,
    firstLogin = firstLogin,
    status = status,
  )
}

internal fun JsonObject.toProviderUser(): ProviderUser =
  ProviderUser(
    firstName = rawStringValue("firstName"),
    lastName = rawStringValue("lastName"),
    fhirPractitionerId = rawStringValue("fhirPractitionerId"),
    practitionerRole = rawStringValue("practitionerRole"),
    role = rawStringValue("role"),
    status = booleanValue("status"),
    id = rawStringValue("id"),
    idNumber = rawStringValue("idNumber"),
    fullNames = rawStringValue("fullNames"),
    phone = rawStringValue("phone"),
    email = rawStringValue("email"),
    locationInfo = objectValue("locationInfo")?.toProviderLocationInfo(),
    communityHealthUnits =
      arrayValue("communityHealthUnits")?.mapNotNull { it.jsonPrimitive.contentOrNull },
  )

internal fun JsonObject.toProviderLocationInfo(): ProviderLocationInfo =
  ProviderLocationInfo(
    facility = rawStringValue("facility"),
    facilityName = rawStringValue("facilityName"),
    ward = rawStringValue("ward"),
    wardName = rawStringValue("wardName"),
    subCounty = rawStringValue("subCounty"),
    subCountyName = rawStringValue("subCountyName"),
    county = rawStringValue("county"),
    countyName = rawStringValue("countyName"),
    country = rawStringValue("country"),
    countryName = rawStringValue("countryName"),
  )

internal fun parseJsonObject(responseBody: String): JsonObject? =
  runCatching { Json.parseToJsonElement(responseBody.trim()).jsonObject }.getOrNull()

internal fun JsonObject.arrayValue(key: String) = this[key]?.jsonArray

internal fun JsonObject.rawStringValue(key: String): String? =
  this[key]?.jsonPrimitive?.contentOrNull

internal fun JsonObject.stringValue(key: String): String? =
  this[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

internal fun JsonObject.objectValue(key: String): JsonObject? =
  runCatching { this[key]?.jsonObject }.getOrNull()

internal fun JsonObject.longValue(key: String): Long? =
  this[key]?.jsonPrimitive?.contentOrNull?.toLongOrNull()

internal fun JsonObject.booleanValue(key: String): Boolean? =
  this[key]?.jsonPrimitive?.booleanOrNull

internal fun extractResponseMessage(responseBody: String, keys: List<String>): String? {
  val trimmedBody = responseBody.trim()
  if (trimmedBody.isBlank()) {
    return null
  }

  if (!trimmedBody.startsWith("{") && !trimmedBody.startsWith("[") && trimmedBody.length <= 160) {
    return trimmedBody
  }

  return runCatching { Json.parseToJsonElement(trimmedBody).jsonObject }
    .getOrNull()
    ?.let { json ->
      keys.firstNotNullOfOrNull { key ->
        json[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
      }
    }
}

internal fun resolveFailureMessage(
  config: ResolvedLoginConfig,
  statusCode: Int,
  responseBody: String,
): String =
  config.responseMessageResolver?.invoke(statusCode, responseBody)?.takeIf(String::isNotBlank)
    ?: extractResponseMessage(responseBody = responseBody, keys = config.responseMessageKeys)
    ?: when {
      statusCode in 400..499 -> config.messages.invalidCredentials
      statusCode >= 500 -> config.messages.serverError
      else -> config.messages.unexpectedError
    }

internal fun resolveSetNewPasswordFailureMessage(
  config: ResolvedSetNewPasswordConfig,
  statusCode: Int,
  responseBody: String,
): String =
  config.responseMessageResolver?.invoke(statusCode, responseBody)?.takeIf(String::isNotBlank)
    ?: extractResponseMessage(responseBody = responseBody, keys = config.responseMessageKeys)
    ?: when {
      statusCode in 400..499 -> config.messages.unexpectedError
      statusCode >= 500 -> config.messages.serverError
      else -> config.messages.unexpectedError
    }

internal fun resolveResetPasswordFailureMessage(
  config: ResolvedResetPasswordConfig,
  statusCode: Int,
  responseBody: String,
): String =
  config.responseMessageResolver?.invoke(statusCode, responseBody)?.takeIf(String::isNotBlank)
    ?: extractResponseMessage(responseBody = responseBody, keys = config.responseMessageKeys)
    ?: when {
      statusCode in 400..499 -> config.messages.invalidOtp
      statusCode >= 500 -> config.messages.serverError
      else -> config.messages.unexpectedError
    }
