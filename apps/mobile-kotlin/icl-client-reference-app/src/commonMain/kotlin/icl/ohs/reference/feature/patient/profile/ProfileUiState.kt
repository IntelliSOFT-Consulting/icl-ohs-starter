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

import icl.ohs.mobile.generated.state.AllergyReactionState
import icl.ohs.mobile.generated.state.PatientAllergyState
import icl.ohs.mobile.generated.state.PatientConditionState
import icl.ohs.mobile.generated.state.PatientContactState
import icl.ohs.mobile.generated.state.PatientImmunizationState
import icl.ohs.mobile.generated.state.PatientMedicationState
import icl.ohs.mobile.generated.state.PatientSummaryState
import icl.ohs.mobile.generated.state.PatientTelecomState

data class ProfileUiState(
  val patient: PatientSummaryState? = null,
  val allergies: List<PatientAllergyState> = emptyList(),
  val allergyReactions: List<AllergyReactionState> = emptyList(),
  val medications: List<PatientMedicationState> = emptyList(),
  val conditions: List<PatientConditionState> = emptyList(),
  val immunizations: List<PatientImmunizationState> = emptyList(),
  val contacts: List<PatientContactState> = emptyList(),
  val telecoms: List<PatientTelecomState> = emptyList(),
)
