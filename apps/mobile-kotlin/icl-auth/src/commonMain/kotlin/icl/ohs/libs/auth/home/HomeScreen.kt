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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A configurable post-login home shell: a top app bar plus, depending on [config]'s
 * [HomeScreenConfig.layoutMode], a navigation drawer, a bottom navigation bar, or both (the
 * default).
 *
 * [HomeScreen] only renders chrome and tracks which item is selected - it does not know about
 * routes or screens. The hosting app owns navigation: it passes the currently [selectedItemId],
 * reacts to [onItemSelected] (switching content, or performing a one-shot action such as logging
 * out), and renders whatever it wants for that selection through [content].
 *
 * ```
 * var selected by rememberSaveable { mutableStateOf("home") }
 * HomeScreen(
 *   selectedItemId = selected,
 *   onItemSelected = { id -> if (id == "logout") onLogout() else selected = id },
 * ) { id ->
 *   when (id) {
 *     "home" -> Dashboard()
 *     "patients" -> PatientListScreen(...)
 *     else -> Unit
 *   }
 * }
 * ```
 *
 * @param config Layout mode, title, and nav item lists. Defaults to a generic drawer + bottom nav
 *   sample (see [HomeNavDefaults]).
 * @param selectedItemId Id of the currently selected [HomeNavItem], used to highlight the matching
 *   drawer/bottom-nav entry.
 * @param onItemSelected Called with a [HomeNavItem.id] when the user taps a drawer, drawer footer,
 *   or bottom nav entry. The drawer (if open) is closed automatically before this fires.
 * @param userLabel Primary line of the default drawer header (e.g. the signed-in user's name).
 *   Ignored if [HomeScreenConfig.drawerHeader] is set.
 * @param userSubLabel Secondary line of the default drawer header (e.g. role or email). Ignored if
 *   [HomeScreenConfig.drawerHeader] is set.
 * @param topBarActions Extra actions rendered at the end of the top app bar, alongside the
 *   hamburger icon.
 * @param content Body of the screen for the currently [selectedItemId].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  config: HomeScreenConfig = HomeScreenConfig(),
  selectedItemId: String,
  onItemSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  userLabel: String = "",
  userSubLabel: String = "",
  topBarActions: @Composable RowScope.() -> Unit = {},
  content: @Composable (String) -> Unit,
) {
  val showDrawer =
    config.layoutMode != HomeLayoutMode.BottomNavOnly && config.drawerItems.isNotEmpty()
  val showBottomNav =
    config.layoutMode != HomeLayoutMode.DrawerOnly && config.bottomNavItems.isNotEmpty()

  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val coroutineScope = rememberCoroutineScope()

  fun selectAndClose(id: String) {
    coroutineScope.launch { drawerState.close() }
    onItemSelected(id)
  }

  val scaffoldContent: @Composable () -> Unit = {
    Scaffold(
      modifier = modifier,
      topBar = {
        TopAppBar(
          title = { Text(config.title, fontWeight = FontWeight.SemiBold) },
          navigationIcon = {
            if (showDrawer) {
              IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Icon(Icons.Filled.Menu, contentDescription = "Open navigation menu")
              }
            }
          },
          actions = topBarActions,
        )
      },
      bottomBar = {
        if (showBottomNav) {
          NavigationBar {
            config.bottomNavItems.forEach { item ->
              val selected = item.id == selectedItemId
              NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.id) },
                icon = { HomeNavIcon(item, selected) },
                label = { Text(item.label) },
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) { content(selectedItemId) }
    }
  }

  if (showDrawer) {
    ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet {
          Column(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
              val customHeader = config.drawerHeader
              if (customHeader != null) customHeader()
              else DefaultDrawerHeader(userLabel, userSubLabel)
              Spacer(modifier = Modifier.height(8.dp))
              config.drawerItems.forEach { item ->
                val selected = item.id == selectedItemId
                NavigationDrawerItem(
                  label = { Text(item.label) },
                  icon = { HomeNavIcon(item, selected) },
                  selected = selected,
                  onClick = { selectAndClose(item.id) },
                  modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
              }
            }
            if (config.drawerFooterItems.isNotEmpty()) {
              HorizontalDivider()
              Column(modifier = Modifier.padding(vertical = 8.dp)) {
                config.drawerFooterItems.forEach { item ->
                  val selected = item.id == selectedItemId
                  NavigationDrawerItem(
                    label = { Text(item.label) },
                    icon = { HomeNavIcon(item, selected) },
                    selected = selected,
                    onClick = { selectAndClose(item.id) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                  )
                }
              }
            }
          }
        }
      },
      content = scaffoldContent,
    )
  } else {
    scaffoldContent()
  }
}

@Composable
private fun HomeNavIcon(item: HomeNavItem, selected: Boolean) {
  val icon = if (selected) item.selectedIcon else item.icon
  val badgeCount = item.badgeCount
  if (badgeCount != null && badgeCount > 0) {
    BadgedBox(badge = { Badge { Text(if (badgeCount > 99) "99+" else badgeCount.toString()) } }) {
      Icon(icon, contentDescription = item.label)
    }
  } else {
    Icon(icon, contentDescription = item.label)
  }
}

@Composable
private fun DefaultDrawerHeader(userLabel: String, userSubLabel: String) {
  Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
    Box(
      modifier =
        Modifier.size(56.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = userLabel.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontWeight = FontWeight.Bold,
      )
    }
    Spacer(modifier = Modifier.height(12.dp))
    if (userLabel.isNotBlank()) {
      Text(
        text = userLabel,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
      )
    }
    if (userSubLabel.isNotBlank()) {
      Text(
        text = userSubLabel,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
