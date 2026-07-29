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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import icl.ohs.libs.auth.IclAuth
import icl.ohs.libs.auth.home.HomeLayoutMode
import icl.ohs.libs.auth.home.HomeScreen
import icl.ohs.libs.auth.home.HomeScreenConfig
import icl.ohs.reference.feature.patient.list.PatientListScreen

/**
 * The reference app's post-login landing screen: [icl.ohs.libs.auth.home.HomeScreen] configured
 * with this app's own destinations (Home, Patients) and actions (Profile, Settings, About, Help,
 * Log out) instead of the library's generic samples.
 *
 * Everything about the surrounding chrome - drawer vs. bottom nav vs. both, which top bar icons
 * show, what's in the overflow menu, whether logout asks for confirmation - is driven by [config].
 * See [ReferenceHomeConfig] for the available knobs; `ReferenceHomeConfig()` reproduces the current
 * default look.
 *
 * Only [ReferenceHomeItemIds.HOME] and [ReferenceHomeItemIds.PATIENTS] are in-place tab switches,
 * rendered below. [ReferenceHomeItemIds.PROFILE] and [ReferenceHomeItemIds.LOGOUT] are one-shot
 * actions handled here; anything else (e.g. ids from a customized item list) is forwarded to
 * [onCustomItemSelected] so callers can wire up their own destinations without forking this screen.
 */
@Composable
fun ReferenceHomeScreen(
  onProfileClick: () -> Unit,
  onPatientClick: (String) -> Unit,
  onLogout: () -> Unit,
  config: ReferenceHomeConfig = ReferenceHomeConfig(layoutMode = HomeLayoutMode.Both),
  onCustomItemSelected: (String) -> Unit = {},
) {
  var selectedTab by rememberSaveable { mutableStateOf(ReferenceHomeItemIds.HOME) }
  var showLogoutConfirm by remember { mutableStateOf(false) }
  val providerUser = IclAuth.currentProviderUser()

  val requestLogout = { if (config.confirmLogout) showLogoutConfirm = true else onLogout() }

  val overflowItems = buildList {
    if (config.showProfileInOverflow) {
      add(
        OverflowMenuItem(
          id = ReferenceHomeItemIds.PROFILE,
          label = "Profile",
          icon = Icons.Filled.AccountCircle,
          onClick = onProfileClick,
        )
      )
    }
    if (config.showLogoutInOverflow) {
      add(
        OverflowMenuItem(
          id = ReferenceHomeItemIds.LOGOUT,
          label = "Log Out",
          icon = Icons.Filled.ExitToApp,
          onClick = requestLogout,
        )
      )
    }
    addAll(config.extraOverflowItems)
  }

  HomeScreen(
    config =
      HomeScreenConfig(
        layoutMode = config.layoutMode,
        title = if (selectedTab == ReferenceHomeItemIds.PATIENTS) "Patients" else "Home",
        bottomNavItems = config.bottomNavItems,
        drawerItems = config.drawerItems,
        drawerFooterItems = config.drawerFooterItems,
      ),
    selectedItemId = selectedTab,
    onItemSelected = { id ->
      when (id) {
        ReferenceHomeItemIds.HOME,
        ReferenceHomeItemIds.PATIENTS -> selectedTab = id

        ReferenceHomeItemIds.PROFILE -> onProfileClick()
        ReferenceHomeItemIds.LOGOUT -> requestLogout()
        // Settings/About/Help have no destination yet in this reference app; anything from a
        // customized item list falls through to the caller too.
        else -> onCustomItemSelected(id)
      }
    },
    userLabel = providerUser?.fullNames.orEmpty(),
    userSubLabel = providerUser?.role.orEmpty(),
    onHeaderClick = onProfileClick,
    topBarActions = {
      TopBarActions(
        notifications = config.notifications,
        sync = config.sync,
        showOverflowMenu = config.showOverflowMenu && overflowItems.isNotEmpty(),
        overflowItems = overflowItems,
      )
    },
  ) { selectedId ->
    when (selectedId) {
      ReferenceHomeItemIds.PATIENTS ->
        PatientListScreen(onPatientClick = onPatientClick, showTopBar = false)

      else -> HomeDashboard(providerUser?.fullNames)
    }
  }

  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text(config.logoutConfirmTitle) },
      text = { Text(config.logoutConfirmMessage) },
      confirmButton = {
        TextButton(
          onClick = {
            showLogoutConfirm = false
            onLogout()
          }
        ) {
          Text(config.logoutConfirmButtonLabel)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirm = false }) { Text(config.logoutCancelButtonLabel) }
      },
    )
  }
}

/**
 * Top app bar actions: an optional notifications icon, an optional sync icon, and an optional
 * overflow menu - each independently toggled off by its caller-supplied value being `null`/empty.
 */
@Composable
private fun TopBarActions(
  notifications: TopBarIconAction?,
  sync: TopBarIconAction?,
  showOverflowMenu: Boolean,
  overflowItems: List<OverflowMenuItem>,
) {
  notifications?.let { TopBarIcon(it) }
  sync?.let { TopBarIcon(it) }
  if (showOverflowMenu) {
    var menuExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded = true }) {
      Icon(Icons.Filled.MoreVert, contentDescription = "More options")
    }
    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
      overflowItems.forEach { item ->
        DropdownMenuItem(
          text = { Text(item.label) },
          leadingIcon = { Icon(item.icon, contentDescription = null) },
          onClick = {
            menuExpanded = false
            item.onClick()
          },
        )
      }
    }
  }
}

@Composable
private fun TopBarIcon(action: TopBarIconAction) {
  IconButton(onClick = action.onClick) {
    val badgeCount = action.badgeCount
    if (badgeCount != null && badgeCount > 0) {
      BadgedBox(badge = { Badge { Text(if (badgeCount > 99) "99+" else badgeCount.toString()) } }) {
        Icon(action.icon, contentDescription = action.contentDescription)
      }
    } else {
      Icon(action.icon, contentDescription = action.contentDescription)
    }
  }
}

@Composable
private fun HomeDashboard(providerName: String?) {
  Column(
    modifier = Modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      imageVector = Icons.Filled.Home,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 12.dp),
    )
    Text(
      text = if (providerName.isNullOrBlank()) "Welcome back" else "Welcome back, $providerName",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.SemiBold,
    )
    Text(
      text = "Use Patients below to find someone, or open the menu for more.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = 8.dp),
    )
  }
}
