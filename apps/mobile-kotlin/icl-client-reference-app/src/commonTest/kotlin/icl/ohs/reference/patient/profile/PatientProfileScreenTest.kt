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
package icl.ohs.reference.feature.patient.profile

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import icl.ohs.library.registry.LocalViewRegistry
import icl.ohs.reference.buildAppViewRegistry
import icl.ohs.reference.data.repository.InMemoryFhirRepository
import icl.ohs.reference.data.repository.PatientRepository
import icl.ohs.reference.data.repository.SamplePatientFixture
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalTestApi::class)
class PatientProfileScreenTest {

  @BeforeTest
  fun setUp() = runTest {
    // PatientRepository no longer auto-seeds sample data (see FhirEngineRepository) - seed the
    // fixture patient and clinical resources this screen expects to find, via an in-memory
    // FhirRepository.
    val repository = InMemoryFhirRepository()
    SamplePatientFixture.resources.forEach { repository.upsert(it) }
    PatientRepository.initialize(repository)
  }

  @Test
  fun knownPatient_rendersNameAndClinicalSections() = runComposeUiTest {
    val registry = buildAppViewRegistry()
    setContent {
      CompositionLocalProvider(LocalViewRegistry provides registry) {
        MaterialTheme { PatientProfileScreen(patientId = "p1", onBack = {}) }
      }
    }

    waitUntil(timeoutMillis = 5_000L) {
      onAllNodesWithText("Amina Diallo").fetchSemanticsNodes().isNotEmpty()
    }
    val scrollable = onNode(hasScrollAction())
    listOf("Amina Diallo", "Allergies", "Medications", "Conditions", "Immunizations").forEach { text
      ->
      scrollable.performScrollToNode(hasText(text))
      assertTrue(
        onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty(),
        "Expected to find '$text' after scrolling the patient profile",
      )
    }
  }
}
