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
package icl.ohs.libs.auth.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import icl.ohs.libs.auth.IclAuth
import icl.ohs.libs.auth.platform.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ProfileViewModel {
  // ProfileRepository/IclAuth restore this from local storage (see AuthSessionStore.
  // providerProfileJson) before this ever runs, so uiState starts populated from disk instead
  // of blank - no network round trip needed just to show what's already known locally.
  var uiState by mutableStateOf(ProfileRepository.getProfile())
    private set

  var isRefreshing by mutableStateOf(false)
    private set

  var errorMessage by mutableStateOf<String?>(null)
    private set

  // Own scope tied to this ViewModel's lifetime instead of a fresh, uncancellable
  // scope per call - cancel() must be invoked (e.g. from a DisposableEffect) when
  // the screen leaves composition so an in-flight refresh doesn't leak.
  private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

  init {
    // Only the very first login on a device (or a persisted profile that was somehow
    // cleared) should ever hit this - IclAuth now restores the profile from local storage on
    // init, so hasProviderProfile is normally already true here. When it isn't, only reach
    // for the network if one is actually there: firing (and failing) a request on every
    // offline launch would just replace "blank" with "blank plus an error banner."
    if (!IclAuth.hasProviderProfile && isNetworkAvailable()) {
      refresh()
    }
  }

  fun refresh() {
    if (isRefreshing) return

    if (!isNetworkAvailable()) {
      errorMessage = "You're offline. Showing the last saved profile details."
      return
    }

    isRefreshing = true
    errorMessage = null
    viewModelScope.launch {
      val result = ProfileRepository.refreshProfile()
      result.fold(
        onSuccess = { uiState = it },
        onFailure = { throwable ->
          // ProfileRepository/IclAuth surface a network-error message here when
          // the /provider/me call fails, including when there's no connectivity.
          errorMessage =
            throwable.message
              ?: "Unable to refresh profile. Check your internet connection and try again."
        },
      )
      isRefreshing = false
    }
  }

  fun dismissError() {
    errorMessage = null
  }

  fun clear() {
    viewModelScope.cancel()
  }
}
