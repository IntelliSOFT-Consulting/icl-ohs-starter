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

// TODO: back this with the browser's `navigator.onLine` for a real check, the way Android uses
// ConnectivityManager. Doing so cleanly across both the js and wasmJs targets sharing this source
// set needs a small JS interop shim; until then, callers fall back to the network call's own
// failure handling - the same behavior this whole check is meant to short-circuit for Android.
actual fun isNetworkAvailable(): Boolean = true
