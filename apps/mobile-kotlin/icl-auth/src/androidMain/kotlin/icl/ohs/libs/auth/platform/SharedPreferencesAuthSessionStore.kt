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
package icl.ohs.libs.auth.platform

import android.content.Context
import icl.ohs.libs.auth.model.AuthSession
import icl.ohs.libs.auth.model.AuthSessionStore
import kotlin.time.Instant

/**
 * Persists the [AuthSession] (field by field) and the signed-in provider's profile (as one JSON
 * blob) to a private Android [android.content.SharedPreferences] file, so relaunching the host app
 * finds the user still signed in - and their profile still populated - instead of asking them to
 * log in again, or showing a blank profile/drawer until a manual refresh. The alternative,
 * [icl.ohs.libs.auth.model.InMemoryAuthSessionStore], only lives as long as the process does.
 *
 * [AuthSession]'s fields are written individually rather than as one JSON blob because it isn't
 * `@Serializable`; it's a plain domain model with no serialization dependency of its own, and this
 * avoids adding one just to support this one storage strategy. The provider profile, in contrast,
 * is stored as a single blob via [icl.ohs.libs.auth.network.toJsonString]/
 * [icl.ohs.libs.auth.network.parseProviderProfile] - it has nested objects and a list, so hand
 * flattening it field by field like the session would be considerably more error-prone.
 */
internal class SharedPreferencesAuthSessionStore(context: Context) : AuthSessionStore {

  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  override var session: AuthSession?
    get() = prefs.readSession()
    set(value) {
      if (value == null) {
        // A cleared session invalidates any persisted profile too - clear it in the same edit so
        // the two never disagree (e.g. a stale profile surviving a logged-out session).
        prefs.edit().clear().apply()
      } else {
        prefs.edit().writeSession(value).apply()
      }
    }

  // Stored as a single JSON blob (see ProviderProfile.toJsonString/parseProviderProfile) rather
  // than field by field like `session` - the profile has nested objects and a list, which would
  // make hand-rolled flattening far more error-prone for little benefit.
  override var providerProfileJson: String?
    get() = prefs.getString(KEY_PROVIDER_PROFILE_JSON, null)
    set(value) {
      val editor = prefs.edit()
      editor.putOrRemoveString(KEY_PROVIDER_PROFILE_JSON, value)
      editor.apply()
    }

  private fun android.content.SharedPreferences.readSession(): AuthSession? {
    val accessToken = getString(KEY_ACCESS_TOKEN, null) ?: return null
    val tokenType = getString(KEY_TOKEN_TYPE, null) ?: return null
    val issuedAtMillis = getLongOrNull(KEY_ISSUED_AT) ?: return null
    return AuthSession(
      accessToken = accessToken,
      tokenType = tokenType,
      refreshToken = getString(KEY_REFRESH_TOKEN, null),
      issuedAt = Instant.fromEpochMilliseconds(issuedAtMillis),
      accessTokenExpiresAt =
        getLongOrNull(KEY_ACCESS_EXPIRES_AT)?.let(Instant::fromEpochMilliseconds),
      refreshTokenExpiresAt =
        getLongOrNull(KEY_REFRESH_EXPIRES_AT)?.let(Instant::fromEpochMilliseconds),
      expiresInSeconds = getLongOrNull(KEY_EXPIRES_IN),
      refreshExpiresInSeconds = getLongOrNull(KEY_REFRESH_EXPIRES_IN),
      notBeforePolicy = getLongOrNull(KEY_NOT_BEFORE_POLICY),
      sessionState = getString(KEY_SESSION_STATE, null),
      scope = getString(KEY_SCOPE, null),
      firstLogin = if (contains(KEY_FIRST_LOGIN)) getBoolean(KEY_FIRST_LOGIN, false) else null,
      status = getString(KEY_STATUS, null),
    )
  }

  private fun android.content.SharedPreferences.getLongOrNull(key: String): Long? =
    if (contains(key)) getLong(key, 0L) else null

  private fun android.content.SharedPreferences.Editor.writeSession(
    session: AuthSession
  ): android.content.SharedPreferences.Editor = apply {
    putString(KEY_ACCESS_TOKEN, session.accessToken)
    putString(KEY_TOKEN_TYPE, session.tokenType)
    putOrRemoveString(KEY_REFRESH_TOKEN, session.refreshToken)
    putLong(KEY_ISSUED_AT, session.issuedAt.toEpochMilliseconds())
    putOrRemoveLong(KEY_ACCESS_EXPIRES_AT, session.accessTokenExpiresAt?.toEpochMilliseconds())
    putOrRemoveLong(KEY_REFRESH_EXPIRES_AT, session.refreshTokenExpiresAt?.toEpochMilliseconds())
    putOrRemoveLong(KEY_EXPIRES_IN, session.expiresInSeconds)
    putOrRemoveLong(KEY_REFRESH_EXPIRES_IN, session.refreshExpiresInSeconds)
    putOrRemoveLong(KEY_NOT_BEFORE_POLICY, session.notBeforePolicy)
    putOrRemoveString(KEY_SESSION_STATE, session.sessionState)
    putOrRemoveString(KEY_SCOPE, session.scope)
    session.firstLogin?.let { putBoolean(KEY_FIRST_LOGIN, it) } ?: remove(KEY_FIRST_LOGIN)
    putOrRemoveString(KEY_STATUS, session.status)
  }

  private fun android.content.SharedPreferences.Editor.putOrRemoveString(
    key: String,
    value: String?,
  ) {
    if (value == null) remove(key) else putString(key, value)
  }

  private fun android.content.SharedPreferences.Editor.putOrRemoveLong(key: String, value: Long?) {
    if (value == null) remove(key) else putLong(key, value)
  }

  private companion object {
    const val PREFS_NAME = "icl_ohs_auth_session"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_TOKEN_TYPE = "token_type"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_ISSUED_AT = "issued_at"
    const val KEY_ACCESS_EXPIRES_AT = "access_expires_at"
    const val KEY_REFRESH_EXPIRES_AT = "refresh_expires_at"
    const val KEY_EXPIRES_IN = "expires_in"
    const val KEY_REFRESH_EXPIRES_IN = "refresh_expires_in"
    const val KEY_NOT_BEFORE_POLICY = "not_before_policy"
    const val KEY_SESSION_STATE = "session_state"
    const val KEY_SCOPE = "scope"
    const val KEY_FIRST_LOGIN = "first_login"
    const val KEY_STATUS = "status"
    const val KEY_PROVIDER_PROFILE_JSON = "provider_profile_json"
  }
}
