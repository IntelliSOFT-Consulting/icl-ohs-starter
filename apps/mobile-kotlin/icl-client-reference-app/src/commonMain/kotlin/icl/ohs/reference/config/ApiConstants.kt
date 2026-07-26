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
package icl.ohs.reference.config

/**
 * Single source of truth for the reference app's network configuration: the API base URL and every
 * endpoint path/URL the app talks to.
 *
 * Screens and screen configs (see `App.kt`, `AuthNavigation.kt`) should reference these constants
 * instead of hardcoding paths, so there is exactly one place to update when the backend contract
 * changes.
 */
object ApiConstants {
  /** Base URL for the provider auth API. Update this when pointing at a different backend. */
  const val BASE_AUTH_URL: String = "https://dsrkeycloak.intellisoftkenya.com/auth"

  // Provider auth endpoints, resolved against BASE_AUTH_URL.
  const val LOGIN_ENDPOINT: String = "/provider/login"
  const val PROVIDER_PROFILE_ENDPOINT: String = "/provider/me"
  const val RESET_PASSWORD_ENDPOINT: String = "/provider/reset-password"

  // Legal/policy pages linked from the auth flow. Replace with your deployed pages.
  const val TERMS_AND_CONDITIONS_URL: String = "https://example.com/terms-and-conditions"
  const val PRIVACY_POLICY_URL: String = "https://example.com/privacy-policy"
}
