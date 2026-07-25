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
package icl.ohs.reference.data.repository

import icl.ohs.mobile.generated.state.AllergyReactionState
import icl.ohs.mobile.generated.state.PatientAllergyState
import icl.ohs.mobile.generated.state.PatientConditionState
import icl.ohs.mobile.generated.state.PatientContactState
import icl.ohs.mobile.generated.state.PatientImmunizationState
import icl.ohs.mobile.generated.state.PatientMedicationState
import icl.ohs.mobile.generated.state.PatientSummaryState
import icl.ohs.mobile.generated.state.PatientTelecomState
import icl.ohs.reference.data.Extraction
import icl.ohs.reference.data.Extraction.extractor
import icl.ohs.reference.data.datasource.allPatientIds
import icl.ohs.reference.data.datasource.patientProfileSearchResult
import icl.ohs.reference.data.datasource.patientSummarySearchResult
import icl.ohs.reference.feature.patient.profile.ProfileUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PatientRepository {

  // FhirPathEvaluator holds mutable state is not concurrent-safe.
  // limitedParallelism(1) serializes all extraction on a single background thread without any
  // explicit locking.
  private val extractorDispatcher = Dispatchers.Default.limitedParallelism(1)

  suspend fun getPatients(): List<PatientSummaryState> =
    withContext(extractorDispatcher) {
      allPatientIds().mapNotNull { id ->
        patientSummarySearchResult(id)?.let {
          extractor.extract<PatientSummaryState>(it).firstOrNull()
        }
      }
    }

  suspend fun getPatientProfile(patientId: String): ProfileUiState =
    withContext(extractorDispatcher) {
      val result = patientProfileSearchResult(patientId) ?: return@withContext ProfileUiState()
      ProfileUiState(
        patient = Extraction.extractor.extract<PatientSummaryState>(result).firstOrNull(),
        allergies = Extraction.extractor.extract<PatientAllergyState>(result),
        allergyReactions = Extraction.extractor.extract<AllergyReactionState>(result),
        medications = Extraction.extractor.extract<PatientMedicationState>(result),
        conditions = Extraction.extractor.extract<PatientConditionState>(result),
        immunizations = Extraction.extractor.extract<PatientImmunizationState>(result),
        contacts =
          Extraction.extractor.extract<PatientContactState>(result).filter {
            it.contactGivenName != null || it.contactFamilyName != null
          },
        telecoms =
          Extraction.extractor.extract<PatientTelecomState>(result).filter {
            it.telecomValue != null
          },
      )
    }
}
