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
package icl.ohs.reference.data.datasource

import dev.ohs.fhir.model.r4.AllergyIntolerance
import dev.ohs.fhir.model.r4.Bundle
import dev.ohs.fhir.model.r4.Condition
import dev.ohs.fhir.model.r4.Immunization
import dev.ohs.fhir.model.r4.MedicationRequest
import dev.ohs.fhir.model.r4.Patient
import dev.ohs.fhir.model.r4.Resource
import icl.ohs.library.model.SearchResult
import iclstarterclientapp.icl_client_reference_app.generated.resources.Res
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

private val fhirJson = Json { ignoreUnknownKeys = true }

private object SampleDataStore {
  private val mutex = Mutex()
  private var initialized = false

  val patients = mutableMapOf<String, Patient>()
  val allergies = mutableMapOf<String, MutableList<AllergyIntolerance>>()
  val medications = mutableMapOf<String, MutableList<MedicationRequest>>()
  val conditions = mutableMapOf<String, MutableList<Condition>>()
  val immunizations = mutableMapOf<String, MutableList<Immunization>>()

  suspend fun ensureInit() {
    if (initialized) return
    mutex.withLock {
      if (initialized) return
      val json = Res.readBytes("files/SampleResourcesBundle.json").decodeToString()
      val bundle = fhirJson.decodeFromString(Bundle.serializer(), json)
      bundle.entry.forEach { entry ->
        when (val res = entry.resource) {
          is Patient -> patients[res.id ?: return@forEach] = res
          is AllergyIntolerance -> {
            val ref = patientId(res.patient.reference?.value)
            ref?.let { allergies.getOrPut(it) { mutableListOf() }.add(res) }
          }
          is MedicationRequest -> {
            val ref = patientId(res.subject.reference?.value)
            ref?.let { medications.getOrPut(it) { mutableListOf() }.add(res) }
          }
          is Condition -> {
            val ref = patientId(res.subject.reference?.value)
            ref?.let { conditions.getOrPut(it) { mutableListOf() }.add(res) }
          }
          is Immunization -> {
            val ref = patientId(res.patient.reference?.value)
            ref?.let { immunizations.getOrPut(it) { mutableListOf() }.add(res) }
          }
          else -> Unit
        }
      }
      initialized = true
    }
  }

  private fun patientId(reference: String?) = reference?.removePrefix("Patient/")?.ifEmpty { null }
}

/** Returns all patient IDs — used by the patient list screen. */
suspend fun allPatientIds(): List<String> {
  SampleDataStore.ensureInit()
  return SampleDataStore.patients.keys.toList()
}

/**
 * Patient list: root = Patient only. No clinical resources needed — the list card shows summary
 * fields available directly on the Patient resource.
 */
suspend fun patientSummarySearchResult(patientId: String): SearchResult<Resource>? {
  SampleDataStore.ensureInit()
  val patient = SampleDataStore.patients[patientId] ?: return null
  return SearchResult(resource = patient)
}

/**
 * Patient profile: root = Patient, all clinical resources in revIncluded. Mirrors a real `GET
 * /Patient/{id}/$everything` response. All section extractors run against this single result.
 */
suspend fun patientProfileSearchResult(patientId: String): SearchResult<Resource>? {
  SampleDataStore.ensureInit()
  val patient = SampleDataStore.patients[patientId] ?: return null
  val revIncluded = buildMap {
    SampleDataStore.allergies[patientId]?.takeIf { it.isNotEmpty() }
      ?.let { put("AllergyIntolerance" to "patient", it) }
    SampleDataStore.medications[patientId]?.takeIf { it.isNotEmpty() }
      ?.let { put("MedicationRequest" to "subject", it) }
    SampleDataStore.conditions[patientId]?.takeIf { it.isNotEmpty() }
      ?.let { put("Condition" to "subject", it) }
    SampleDataStore.immunizations[patientId]?.takeIf { it.isNotEmpty() }
      ?.let { put("Immunization" to "patient", it) }
  }
  return SearchResult(
    resource = patient,
    included = mapOf("patient" to listOf(patient)),
    revIncluded = revIncluded.ifEmpty { null },
  )
}
