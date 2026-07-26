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
package icl.ohs.libs.auth.home

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * One entry in either the navigation drawer or the bottom navigation bar of [HomeScreen].
 *
 * [HomeScreen] treats every item the same way: it renders it, tracks selection, and reports clicks
 * through `onItemSelected(id)`. Whether an [id] is a switchable destination (e.g. `"home"`) or a
 * one-shot action (e.g. `"logout"`) is entirely up to the hosting app - [HomeScreen] has no opinion
 * on it.
 *
 * @param id Stable identifier the host uses to know which item was selected/clicked.
 * @param label Text shown next to the icon.
 * @param icon Icon shown while the item is not selected.
 * @param selectedIcon Icon shown while the item is selected. Defaults to [icon] when a hosting app
 *   doesn't want a separate filled/outlined pair.
 * @param badgeCount Optional count rendered as a small badge on top of the icon (e.g. unread
 *   notifications). `null` or `0` hides the badge.
 */
data class HomeNavItem(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val selectedIcon: ImageVector = icon,
  val badgeCount: Int? = null,
)

/** Controls which chrome [HomeScreen] renders around its content. */
enum class HomeLayoutMode {
  /** Show only the navigation drawer, opened from a hamburger icon in the top bar. */
  DrawerOnly,

  /** Show only the bottom navigation bar. */
  BottomNavOnly,

  /** Show both the drawer and the bottom navigation bar. This is the default. */
  Both,
}
