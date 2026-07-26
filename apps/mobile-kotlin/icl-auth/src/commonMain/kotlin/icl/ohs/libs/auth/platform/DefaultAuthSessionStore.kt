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

import icl.ohs.libs.auth.model.AuthSessionStore

/**
 * The [AuthSessionStore] [icl.ohs.libs.auth.IclAuthConfig] uses when a hosting app doesn't supply
 * its own - platform-appropriate persistence with zero setup on the host app's part.
 *
 * Android persists the session to a private SharedPreferences file (via
 * [SharedPreferencesAuthSessionStore]), backed by a Context the library captures itself at process
 * start - no manifest or Application changes needed in the host app. Every other target currently
 * falls back to [icl.ohs.libs.auth.model.InMemoryAuthSessionStore] (session lost when the process
 * dies); extend the matching actual in iosMain/jvmMain/webMain as those platforms need to survive a
 * restart too.
 *
 * Hosting apps that want different behavior (Keychain, encrypted prefs, a backend-synced store,
 * ...) can still override this by passing their own `sessionStore` to
 * [icl.ohs.libs.auth.IclAuthConfig].
 */
expect fun createDefaultAuthSessionStore(): AuthSessionStore
