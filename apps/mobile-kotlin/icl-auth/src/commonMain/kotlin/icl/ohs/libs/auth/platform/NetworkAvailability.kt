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

/**
 * Best-effort, synchronous check for whether the device currently reports network connectivity.
 * Used to skip a network call that's all but certain to fail - and the confusing error banner that
 * would follow it - when there's obviously no connection (e.g. an automatic provider-profile
 * refresh on app launch should quietly fall back to whatever is cached locally instead).
 *
 * This is deliberately best-effort: a `true` result does not guarantee a request will succeed
 * (captive portals, DNS issues, a server that's actually down, ...), so callers still need to
 * handle failures from the network call itself. It only exists to avoid the *obviously* doomed
 * attempt.
 *
 * Only Android has a real implementation today (via `ConnectivityManager`); other targets report
 * `true` unconditionally, matching the current platform-support gap for persistence itself (see
 * `createDefaultAuthSessionStore`) - callers on those platforms fall back to relying on the network
 * call's own failure handling, exactly as they did before this check existed.
 */
expect fun isNetworkAvailable(): Boolean
