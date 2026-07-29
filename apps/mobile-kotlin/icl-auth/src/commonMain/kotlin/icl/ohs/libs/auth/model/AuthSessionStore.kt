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
package icl.ohs.libs.auth.model

/**
 * Pluggable persistence for the current [AuthSession] (e.g. encrypted prefs, keychain), and
 * optionally the signed-in user's provider profile alongside it.
 *
 * [providerProfileJson] stores the provider profile (fetched from `/provider/me` at login and on
 * refresh) as a JSON blob rather than a typed model, matching how [session] itself is persisted -
 * this keeps the interface storage-agnostic instead of assuming a serialization library. It
 * defaults to a no-op (get returns `null`, set does nothing) so existing implementers of this
 * interface aren't broken by its addition; override it to actually persist the profile, the way
 * [icl.ohs.libs.auth.platform.SharedPreferencesAuthSessionStore] does on Android. Without an
 * override, the profile is refetched from the network every time the process restarts.
 */
interface AuthSessionStore {
  var session: AuthSession?

  var providerProfileJson: String?
    get() = null
    set(value) {}
}

/** Default in-memory store; neither the session nor the provider profile survive process death. */
object InMemoryAuthSessionStore : AuthSessionStore {
  override var session: AuthSession? = null
  override var providerProfileJson: String? = null
}
