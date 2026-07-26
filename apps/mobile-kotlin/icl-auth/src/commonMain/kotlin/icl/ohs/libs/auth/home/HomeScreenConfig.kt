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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable

/**
 * Configuration for [HomeScreen].
 *
 * Every field has a sensible sample default, so `HomeScreenConfig()` alone renders a fully working
 * (if generic) home shell with both a drawer and a bottom navigation bar - swap in your own
 * [HomeNavItem] lists and title to make it yours.
 *
 * @param layoutMode Which chrome to render. Defaults to [HomeLayoutMode.Both].
 * @param title Text shown in the top app bar.
 * @param bottomNavItems Destinations shown in the bottom navigation bar. Ignored when [layoutMode]
 *   is [HomeLayoutMode.DrawerOnly]. Keep this short (3-5 items) - that's what the bottom nav is
 *   for.
 * @param drawerItems Primary, scrollable destinations shown at the top of the drawer. Ignored when
 *   [layoutMode] is [HomeLayoutMode.BottomNavOnly].
 * @param drawerFooterItems Secondary actions pinned to the bottom of the drawer, below a divider
 *   (things like Settings, Help, and Log out). Ignored when [layoutMode] is
 *   [HomeLayoutMode.BottomNavOnly].
 * @param drawerHeader Optional custom header rendered above [drawerItems]. Defaults to a simple
 *   name/subtitle block driven by the `userLabel`/`userSubLabel` parameters on [HomeScreen].
 */
data class HomeScreenConfig(
  val layoutMode: HomeLayoutMode = HomeLayoutMode.Both,
  val title: String = "Home",
  val bottomNavItems: List<HomeNavItem> = HomeNavDefaults.bottomNavItems,
  val drawerItems: List<HomeNavItem> = HomeNavDefaults.drawerItems,
  val drawerFooterItems: List<HomeNavItem> = HomeNavDefaults.drawerFooterItems,
  val drawerHeader: (@Composable () -> Unit)? = null,
)

/**
 * Sample [HomeNavItem] sets used as the defaults for [HomeScreenConfig]. These are intentionally
 * generic (not tied to any domain) so hosting apps see a working drawer + bottom nav out of the
 * box, and have a concrete pattern to copy when they define their own items.
 */
object HomeNavDefaults {

  /** A compact set of everyday destinations, sized for a bottom navigation bar. */
  val bottomNavItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(
        id = "home",
        label = "Home",
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Filled.Dashboard,
      ),
      HomeNavItem(
        id = "search",
        label = "Search",
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
      ),
      HomeNavItem(
        id = "notifications",
        label = "Alerts",
        icon = Icons.Outlined.Notifications,
        selectedIcon = Icons.Filled.Notifications,
      ),
      HomeNavItem(
        id = "account",
        label = "Account",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person,
      ),
    )

  /** A richer set of destinations for the scrollable, primary section of the drawer. */
  val drawerItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(
        id = "home",
        label = "Home",
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Filled.Dashboard,
      ),
      HomeNavItem(
        id = "messages",
        label = "Messages",
        icon = Icons.Filled.Mail,
        selectedIcon = Icons.Filled.Mail,
      ),
      HomeNavItem(
        id = "favorites",
        label = "Favorites",
        icon = Icons.Outlined.Favorite,
        selectedIcon = Icons.Filled.Favorite,
      ),
    )

  /** Secondary actions pinned below a divider at the bottom of the drawer. */
  val drawerFooterItems: List<HomeNavItem> =
    listOf(
      HomeNavItem(
        id = "settings",
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
      ),
      HomeNavItem(
        id = "help",
        label = "Help & Support",
        icon = Icons.Filled.HelpOutline,
        selectedIcon = Icons.Filled.HelpOutline,
      ),
      HomeNavItem(
        id = "about",
        label = "About",
        icon = Icons.Outlined.Info,
        selectedIcon = Icons.Filled.Info,
      ),
      HomeNavItem(
        id = "logout",
        label = "Log Out",
        icon = Icons.Filled.ExitToApp,
        selectedIcon = Icons.Filled.ExitToApp,
      ),
    )
}
