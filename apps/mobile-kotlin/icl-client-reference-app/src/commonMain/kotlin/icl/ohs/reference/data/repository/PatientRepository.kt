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

import dev.ohs.fhir.model.r4.AllergyIntolerance
import dev.ohs.fhir.model.r4.Bundle
import dev.ohs.fhir.model.r4.Condition
import dev.ohs.fhir.model.r4.Immunization
import dev.ohs.fhir.model.r4.MedicationRequest
import dev.ohs.fhir.model.r4.Resource
import icl.ohs.library.model.SearchResult
import icl.ohs.mobile.generated.state.AllergyReactionState
import icl.ohs.mobile.generated.state.PatientAllergyState
import icl.ohs.mobile.generated.state.PatientConditionState
import icl.ohs.mobile.generated.state.PatientContactState
import icl.ohs.mobile.generated.state.PatientImmunizationState
import icl.ohs.mobile.generated.state.PatientMedicationState
import icl.ohs.mobile.generated.state.PatientSummaryState
import icl.ohs.mobile.generated.state.PatientTelecomState
import icl.ohs.reference.data.Extraction
import icl.ohs.reference.feature.patient.profile.ProfileUiState
import iclstarterclientapp.icl_client_reference_app.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Patient data + extraction, backed by a [FhirRepository] - a [FhirEngineRepository] (real, on-disk
 * FHIR Engine database) in production, an in-memory test double in tests.
 *
 * [initialize] must be called once, before [getPatients]/[getPatientProfile] are used - each
 * platform entry point does this at startup, the same way [icl.ohs.libs.auth.IclAuth.initialize]
 * is.
 */
object PatientRepository {

  private const val QUESTIONNAIRE_ID = "add-clinical-data"
  private const val QUESTIONNAIRE_ASSET_PATH =
    "files/questionnaires/Questionnaire-$QUESTIONNAIRE_ID.json"
  private const val SAMPLE_PATIENTS_ASSET_PATH = "files/sample-data/patients-1000-bundle.json"

  private lateinit var repository: FhirRepository

  // FhirPathEvaluator holds mutable state is not concurrent-safe.
  // limitedParallelism(1) serializes all extraction on a single background thread without any
  // explicit locking.
  private val extractorDispatcher = Dispatchers.Default.limitedParallelism(1)

  // Fire-and-forget home for startup seeding - independent of any screen's lifecycle, so it isn't
  // cancelled by navigation while it's still reading the asset/writing to the engine.
  private val seedScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  fun initialize(repository: FhirRepository) {
    this.repository = repository
  }

  /**
   * Bumps every time the underlying [FhirRepository] is written to - including by the
   * fire-and-forget [seedSamplePatientsAsync]/[seedQuestionnaireAsync] launches, which finish some
   * time after app start. Screens collect this (rather than doing a one-shot fetch) so they pick up
   * that seeded data automatically instead of showing an empty state forever if they happened to
   * load first.
   */
  fun revision(): StateFlow<Long> = repository.revision

  /**
   * Reads the bundled "Add Clinical Data" Questionnaire out of app assets
   * (`composeResources/files/questionnaires`) and upserts it into the FHIR engine, so it's on disk
   * and available via [getAddClinicalDataQuestionnaire] without a network round trip. Each
   * production entry point calls this once, right after [initialize].
   *
   * Safe to call on every launch: [FhirRepository.upsert] updates the existing `Questionnaire/
   * add-clinical-data` record in place rather than duplicating it.
   */
  fun seedQuestionnaireAsync() {
    seedScope.launch {
      val json = Res.readBytes(QUESTIONNAIRE_ASSET_PATH).decodeToString()
      val questionnaire = FhirJson.instance.decodeFromString(Resource.serializer(), json)
      repository.upsert(questionnaire)
    }
  }

  /**
   * The bundled "Add Clinical Data" Questionnaire, read back from the engine (not the asset) -
   * `null` until [seedQuestionnaireAsync] has finished at least once.
   */
  suspend fun getAddClinicalDataQuestionnaire(): QuestionnaireForm? =
    repository.get("Questionnaire", QUESTIONNAIRE_ID)?.let(::parseQuestionnaireForm)

  /**
   * Loads the bundled 1000-patient sample data set (`sample-data/patients-1000-bundle.json`) into
   * the FHIR engine - but only when the Patient table is still empty, so this is effectively a
   * one-time first-launch seed rather than something that re-runs (and re-upserts ~5000 resources)
   * on every app start. Deleting all patients from the engine re-arms it on the next launch.
   */
  fun seedSamplePatientsAsync() {
    seedScope.launch {
      if (repository.all("Patient").isNotEmpty()) return@launch
      val json = Res.readBytes(SAMPLE_PATIENTS_ASSET_PATH).decodeToString()
      val bundle = FhirJson.instance.decodeFromString(Bundle.serializer(), json)
      repository.upsert(bundle)
    }
  }

  /** Saves a completed "Add Clinical Data" form as a new FHIR resource for [patientId]. */
  suspend fun addClinicalData(patientId: String, category: String, display: String, date: String?) {
    repository.upsert(buildClinicalDataResource(patientId, category, display, date))
  }

  suspend fun getPatients(): List<PatientSummaryState> =
    withContext(extractorDispatcher) {
      repository.all("Patient").mapNotNull { patient ->
        Extraction.extractor
          .extract<PatientSummaryState>(SearchResult(resource = patient))
          .firstOrNull()
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

  /**
   * Patient profile: root = Patient, all clinical resources in revIncluded. Mirrors a real `GET
   * /Patient/{id}/$everything` response. All section extractors run against this single result.
   *
   * [FhirRepository.all] isn't scoped by reference, so each clinical resource type is fetched in
   * full and filtered client-side by its `patient`/`subject` reference - fine at reference-app
   * scale; push this down to the engine's own search parameters once available.
   */
  private suspend fun patientProfileSearchResult(patientId: String): SearchResult<Resource>? {
    val patient = repository.get("Patient", patientId) ?: return null

    val allergies =
      repository.all("AllergyIntolerance").filterIsInstance<AllergyIntolerance>().filter {
        referencedPatientId(it.patient.reference?.value) == patientId
      }
    val medications =
      repository.all("MedicationRequest").filterIsInstance<MedicationRequest>().filter {
        referencedPatientId(it.subject.reference?.value) == patientId
      }
    val conditions =
      repository.all("Condition").filterIsInstance<Condition>().filter {
        referencedPatientId(it.subject.reference?.value) == patientId
      }
    val immunizations =
      repository.all("Immunization").filterIsInstance<Immunization>().filter {
        referencedPatientId(it.patient.reference?.value) == patientId
      }

    val revIncluded = buildMap {
      allergies.takeIf { it.isNotEmpty() }?.let { put("AllergyIntolerance" to "patient", it) }
      medications.takeIf { it.isNotEmpty() }?.let { put("MedicationRequest" to "subject", it) }
      conditions.takeIf { it.isNotEmpty() }?.let { put("Condition" to "subject", it) }
      immunizations.takeIf { it.isNotEmpty() }?.let { put("Immunization" to "patient", it) }
    }
    return SearchResult(
      resource = patient,
      included = mapOf("patient" to listOf(patient)),
      revIncluded = revIncluded.ifEmpty { null },
    )
  }

  private fun referencedPatientId(reference: String?) =
    reference?.removePrefix("Patient/")?.ifEmpty { null }
}
