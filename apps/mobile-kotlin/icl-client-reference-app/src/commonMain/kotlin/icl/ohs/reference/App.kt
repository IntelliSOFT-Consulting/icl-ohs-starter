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
package icl.ohs.reference

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import icl.ohs.library.registry.LocalViewRegistry
import icl.ohs.libs.auth.IclAuth
import icl.ohs.libs.auth.IclAuthConfig
import icl.ohs.libs.auth.profile.ProfileScreen
import icl.ohs.libs.auth.profile.ProfileViewModel
import icl.ohs.reference.config.ApiConstants
import icl.ohs.reference.feature.home.ReferenceHomeScreen
import icl.ohs.reference.feature.patient.profile.PatientProfileScreen

private const val HOME_ROUTE = "home"
private const val PROFILE_ROUTE = "profile"
private const val PATIENT_PROFILE_ROUTE = "patientProfile"
private const val PATIENT_ID_ARG = "patientId"

// IclAuthConfig's sessionStore defaults to a persistent, platform-appropriate store (see
// icl-auth's DefaultAuthSessionStore), so a relaunch keeps the user signed in without this app
// having to wire anything up itself.
private val AUTH_CONFIG =
  IclAuthConfig(
    baseAuthUrl = ApiConstants.BASE_AUTH_URL,
    providerProfileEndpoint = ApiConstants.PROVIDER_PROFILE_ENDPOINT,
  )

@Composable
fun App() {
  remember(AUTH_CONFIG) { IclAuth.initialize(AUTH_CONFIG) }
  val registry = remember { buildAppViewRegistry() }

  CompositionLocalProvider(LocalViewRegistry provides registry) {
    OhsPlayerTheme {
      var isLoggedIn by rememberSaveable { mutableStateOf(IclAuth.hasValidAccessToken()) }

      if (isLoggedIn) {
        ReferenceAppNavigation(
          onLogout = {
            // Clears the persisted session too, via whatever AuthSessionStore AUTH_CONFIG was
            // built with - otherwise a "logged out" relaunch would just sign back in.
            IclAuth.clearSession()
            isLoggedIn = false
          }
        )
      } else {
        AuthNavigation(onAuthenticated = { isLoggedIn = true })
      }
    }
  }
}

@Composable
private fun ReferenceAppNavigation(onLogout: () -> Unit) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = HOME_ROUTE) {

    // Post-login landing screen: configurable drawer + bottom nav shell.
    composable(HOME_ROUTE) {
      ReferenceHomeScreen(
        onProfileClick = { navController.navigate(PROFILE_ROUTE) },
        onPatientClick = { id -> navController.navigate("$PATIENT_PROFILE_ROUTE/$id") },
        onLogout = { onLogout() },
      )
    }

    // New Profile Screen
    composable(PROFILE_ROUTE) {
      val viewModel = remember { ProfileViewModel() }
      ProfileScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
    }

    // Patient IPS summary
    composable(
      route = "$PATIENT_PROFILE_ROUTE/{$PATIENT_ID_ARG}",
      arguments = listOf(navArgument(PATIENT_ID_ARG) { type = NavType.StringType }),
    ) { back ->
      val patientId = back.arguments?.read { getString(PATIENT_ID_ARG) }.orEmpty()
      PatientProfileScreen(patientId = patientId, onBack = { navController.popBackStack() })
    }
  }
}
