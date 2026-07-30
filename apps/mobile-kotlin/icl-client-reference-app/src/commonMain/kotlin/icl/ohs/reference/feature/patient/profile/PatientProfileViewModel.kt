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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import icl.ohs.reference.data.repository.PatientRepository
import icl.ohs.reference.data.repository.QuestionnaireForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IpsPatientProfileViewModel(private val patientId: String) : ViewModel() {
  private val _uiState = MutableStateFlow<ProfileUiState?>(null)
  val uiState: StateFlow<ProfileUiState?> = _uiState.asStateFlow()

  // Read back from the engine (seeded into it at launch by
  // PatientRepository.seedQuestionnaireAsync)
  // rather than the app asset directly, so this doubles as proof the seed actually persisted.
  private val _addClinicalDataForm = MutableStateFlow<QuestionnaireForm?>(null)
  val addClinicalDataForm: StateFlow<QuestionnaireForm?> = _addClinicalDataForm.asStateFlow()

  init {
    // Re-fetches both the profile and the Add Clinical Data form on every engine write, not just
    // once at screen load - this is what picks up the fire-and-forget startup seeding, and what
    // refreshes the profile after addClinicalData's own upsert below, without a separate manual
    // refresh path.
    viewModelScope.launch {
      PatientRepository.revision().collect {
        _uiState.value = PatientRepository.getPatientProfile(patientId)
        _addClinicalDataForm.value = PatientRepository.getAddClinicalDataQuestionnaire()
      }
    }
  }

  fun addClinicalData(category: String, display: String, date: String?) {
    viewModelScope.launch { PatientRepository.addClinicalData(patientId, category, display, date) }
  }
}
