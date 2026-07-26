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
package icl.ohs.reference.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import icl.ohs.libs.auth.home.HomeLayoutMode
import icl.ohs.libs.auth.home.HomeNavItem

/**
 * Ids [ReferenceHomeScreen] gives built-in meaning to: [HOME]/[PATIENTS] switch the in-place tab
 * content, [PROFILE] and [LOGOUT] trigger navigation/logout. If you override
 * [ReferenceHomeConfig.bottomNavItems], [ReferenceHomeConfig.drawerItems], or
 * [ReferenceHomeConfig.drawerFooterItems] with your own [HomeNavItem]s, reuse these ids to keep
 * that behavior - anything else is forwarded to `onCustomItemSelected` on [ReferenceHomeScreen].
 */
object ReferenceHomeItemIds {
  const val HOME = "home"
  const val PATIENTS = "patients"
  const val PROFILE = "profile"
  const val SETTINGS = "settings"
  const val ABOUT = "about"
  const val HELP = "help"
  const val LOGOUT = "logout"
}

/**
 * A single icon button in the top app bar (notifications, sync, ...). Set the corresponding
 * [ReferenceHomeConfig] field to `null` to omit it entirely.
 */
data class TopBarIconAction(
  val icon: ImageVector,
  val contentDescription: String,
  val badgeCount: Int? = null,
  val onClick: () -> Unit = {},
)

/** A single entry in the top app bar's overflow ("more") menu. */
data class OverflowMenuItem(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val onClick: () -> Unit,
)

/**
 * Everything about [ReferenceHomeScreen] a caller might want to dial up or down: which chrome shows
 * (drawer, bottom nav, or both), which destinations populate each, which top bar icons appear,
 * what's tucked into the overflow menu, and whether logging out asks for confirmation.
 *
 * Every field defaults to this app's out-of-the-box look, so `ReferenceHomeConfig()` alone
 * reproduces the current behavior. Some starting points:
 * - Bottom-nav-only app: `layoutMode = HomeLayoutMode.BottomNavOnly`.
 * - Drawer-only app: `layoutMode = HomeLayoutMode.DrawerOnly`.
 * - No notifications icon: `notifications = null`.
 * - No sync icon: `sync = null`.
 * - No overflow menu at all: `showOverflowMenu = false`.
 * - Overflow menu with just Log out, no Profile shortcut: `showProfileInOverflow = false`.
 * - Skip the "are you sure" prompt on logout: `confirmLogout = false`.
 */
data class ReferenceHomeConfig(
  val layoutMode: HomeLayoutMode = HomeLayoutMode.Both,
  val bottomNavItems: List<HomeNavItem> = ReferenceHomeDefaults.bottomNavItems,
  val drawerItems: List<HomeNavItem> = ReferenceHomeDefaults.drawerItems,
  val drawerFooterItems: List<HomeNavItem> = ReferenceHomeDefaults.drawerFooterItems,
  val notifications: TopBarIconAction? = ReferenceHomeDefaults.notifications,
  val sync: TopBarIconAction? = ReferenceHomeDefaults.sync,
  val showOverflowMenu: Boolean = true,
  val showProfileInOverflow: Boolean = true,
  val showLogoutInOverflow: Boolean = true,
  val extraOverflowItems: List<OverflowMenuItem> = emptyList(),
  val confirmLogout: Boolean = true,
  val logoutConfirmTitle: String = "Log out?",
  val logoutConfirmMessage: String = "You'll need to sign in again to continue.",
  val logoutConfirmButtonLabel: String = "Log Out",
  val logoutCancelButtonLabel: String = "Cancel",
)

/** Default item lists and top bar icons behind [ReferenceHomeConfig]'s defaults. */
object ReferenceHomeDefaults {

  val bottomNavItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(ReferenceHomeItemIds.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
      HomeNavItem(
        ReferenceHomeItemIds.PATIENTS,
        "Patients",
        Icons.Outlined.Home,
        Icons.Filled.Home,
      ),
      HomeNavItem(
        ReferenceHomeItemIds.PROFILE,
        "Profile",
        Icons.Outlined.AccountCircle,
        Icons.Filled.AccountCircle,
      ),
    )

  val drawerItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(ReferenceHomeItemIds.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
      HomeNavItem(ReferenceHomeItemIds.PATIENTS, "Patients", Icons.Outlined.Home, Icons.Filled.Home),
    )

  val drawerFooterItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(
        ReferenceHomeItemIds.PROFILE,
        "My Profile",
        Icons.Outlined.AccountCircle,
        Icons.Filled.AccountCircle,
      ),
      HomeNavItem(
        ReferenceHomeItemIds.SETTINGS,
        "Settings",
        Icons.Outlined.Settings,
        Icons.Filled.Settings,
      ),
      HomeNavItem(ReferenceHomeItemIds.ABOUT, "About", Icons.Outlined.Info, Icons.Filled.Info),
      HomeNavItem(
        ReferenceHomeItemIds.HELP,
        "Help & Support",
        Icons.Filled.Info,
        Icons.Filled.Info,
      ),
      HomeNavItem(
        ReferenceHomeItemIds.LOGOUT,
        "Log Out",
        Icons.Filled.ExitToApp,
        Icons.Filled.ExitToApp,
      ),
    )

  val notifications: TopBarIconAction =
    TopBarIconAction(icon = Icons.Filled.Notifications, contentDescription = "Notifications")

  val sync: TopBarIconAction =
    TopBarIconAction(icon = Icons.Filled.Refresh, contentDescription = "Sync")
}
