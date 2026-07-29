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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Holds the application [Context] captured by [AppContextInitializer], for the library's own
 * platform code (e.g. [SharedPreferencesAuthSessionStore]) to use.
 */
internal object AppContextHolder {
  lateinit var appContext: Context
    private set

  fun attach(context: Context) {
    if (!::appContext.isInitialized) {
      appContext = context
    }
  }

  /** Whether [attach] has run yet - guards uses of [appContext] before the provider initializes. */
  fun isAttached(): Boolean = ::appContext.isInitialized
}

/**
 * A manifest-registered, no-op [ContentProvider] whose only job is running [onCreate] - which
 * Android guarantees happens before [android.app.Application.onCreate] and any Activity in the host
 * app - to hand [AppContextHolder] an application [Context].
 *
 * This is declared in the library's own `AndroidManifest.xml` and merged automatically into every
 * consuming app's manifest, so hosting apps get a working default
 * [icl.ohs.libs.auth.model.AuthSessionStore] without writing any Android-specific code themselves.
 */
class AppContextInitializer : ContentProvider() {
  override fun onCreate(): Boolean {
    context?.applicationContext?.let(AppContextHolder::attach)
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?,
  ): Cursor? = null

  override fun getType(uri: Uri): String? = null

  override fun insert(uri: Uri, values: ContentValues?): Uri? = null

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?,
  ): Int = 0
}
