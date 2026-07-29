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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * @param onHeaderClick Called when the default drawer header (avatar + name row) is tapped - e.g.
 *   to open a full profile screen. Leave `null` (the default) to render the header as static,
 *   non-interactive text. Ignored if [HomeScreenConfig.drawerHeader] is set.
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
  onHeaderClick: (() -> Unit)? = null,
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

  fun closeAndRun(action: () -> Unit) {
    coroutineScope.launch { drawerState.close() }
    action()
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
    ) { innerPadding ->
      Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize()) {
          content(selectedItemId)
        }
        if (showBottomNav) {
          FloatingBottomNavigationBar(
            items = config.bottomNavItems,
            selectedItemId = selectedItemId,
            onItemSelected = onItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
          )
        }
      }
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
              else
                DefaultDrawerHeader(
                  userLabel,
                  userSubLabel,
                  onClick = onHeaderClick?.let { click -> { closeAndRun(click) } },
                )
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

/**
 * A pill-shaped bottom navigation bar that floats above the screen edge with rounded corners and a
 * soft shadow - the look most professional apps use instead of an edge-to-edge [NavigationBar]. It
 * is meant to be layered on top of scrolling content (see [HomeScreen]'s content [Box]), not placed
 * in [Scaffold]'s `bottomBar` slot, so content can scroll underneath it.
 */
@Composable
private fun FloatingBottomNavigationBar(
  items: List<HomeNavItem>,
  selectedItemId: String,
  onItemSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier =
      modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
    shape = RoundedCornerShape(28.dp),
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    shadowElevation = 8.dp,
    tonalElevation = 3.dp,
  ) {
    NavigationBar(
      containerColor = Color.Transparent,
      tonalElevation = 0.dp,
      windowInsets = WindowInsets(0.dp),
    ) {
      items.forEach { item ->
        val selected = item.id == selectedItemId
        NavigationBarItem(
          selected = selected,
          onClick = { onItemSelected(item.id) },
          icon = { HomeNavIcon(item, selected) },
          label = { Text(item.label) },
          colors =
            NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }
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

/**
 * Default drawer header: avatar and name/role side by side in a row, mirroring the profile screen's
 * own header. Tappable (with a trailing chevron as an affordance) when [onClick] is provided, so it
 * can double as a shortcut into a full profile screen.
 */
@Composable
private fun DefaultDrawerHeader(
  userLabel: String,
  userSubLabel: String,
  onClick: (() -> Unit)? = null,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(24.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier =
        Modifier.size(56.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = drawerInitials(userLabel),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontWeight = FontWeight.Bold,
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
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
    if (onClick != null) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

/**
 * Two-letter initials from a full name (e.g. "Japheth Kiprotich" -> "JK"), matching how
 * [icl.ohs.libs.auth.profile.ProfileUiState.initials] derives initials from separate first/last
 * name fields - [HomeScreen] only has a single [userLabel] string to work with, so this splits it
 * on whitespace instead: first letter of the first word plus first letter of the last word. Falls
 * back to a single letter for a one-word name, or "?" if [userLabel] is blank.
 */
private fun drawerInitials(userLabel: String): String {
  val words = userLabel.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
  return when {
    words.isEmpty() -> "?"
    words.size == 1 -> words.first().take(1).uppercase()
    else -> (words.first().take(1) + words.last().take(1)).uppercase()
  }
}
