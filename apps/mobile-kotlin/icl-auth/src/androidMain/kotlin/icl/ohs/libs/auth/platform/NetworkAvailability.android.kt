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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Uses the application [Context] captured by [AppContextInitializer] - the same one
 * [SharedPreferencesAuthSessionStore] relies on - so this works with zero setup on the host app's
 * part, just like the rest of this package.
 */
actual fun isNetworkAvailable(): Boolean {
  if (!AppContextHolder.isAttached()) return true

  val connectivityManager =
    AppContextHolder.appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
      as? ConnectivityManager ?: return true

  val network = connectivityManager.activeNetwork ?: return false
  val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
  return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
