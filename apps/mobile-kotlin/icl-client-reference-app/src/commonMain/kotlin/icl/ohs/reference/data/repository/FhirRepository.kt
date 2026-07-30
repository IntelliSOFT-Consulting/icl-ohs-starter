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

import dev.ohs.fhir.model.r4.Bundle
import dev.ohs.fhir.model.r4.Resource
import kotlinx.coroutines.flow.StateFlow

/**
 * Minimal persistence seam for FHIR resources, backed by [FhirEngineRepository] (a real, on-disk
 * FHIR Engine database) in production and an in-memory test double in tests. Keeps
 * [PatientRepository] from depending on a concrete [dev.ohs.fhir.FhirEngine] instance directly.
 */
interface FhirRepository {
  /**
   * Incremented on every successful [upsert]. Implementers must bump this after each write so
   * observers know to re-query. [PatientRepository] exposes this via `revision()` so the patient
   * list/profile screens re-fetch whenever it changes - including once the fire-and-forget startup
   * seeding finishes, rather than only doing a one-shot fetch when the screen first loads.
   */
  val revision: StateFlow<Long>

  suspend fun upsert(resource: Resource)

  suspend fun upsert(bundle: Bundle): Int

  suspend fun get(resourceType: String, id: String): Resource?

  suspend fun all(resourceType: String): List<Resource>
}
