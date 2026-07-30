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

import android.app.Application
import dev.ohs.fhir.engine.FhirEngineConfiguration
import dev.ohs.fhir.engine.FhirEngineProvider
import icl.ohs.reference.data.repository.FhirEngineRepository
import icl.ohs.reference.data.repository.PatientRepository

/**
 * Initializes the on-device FHIR Engine database and wires it into [PatientRepository] once, at
 * process start - before [MainActivity] (or any Activity) exists - instead of in `onCreate` of the
 * first Activity, so it isn't accidentally re-run on an Activity recreation (e.g. rotation).
 */
class ReferenceApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    FhirEngineProvider.init(FhirEngineConfiguration(), applicationContext)
    val fhirEngine = FhirEngineProvider.getInstance(applicationContext)
    PatientRepository.initialize(FhirEngineRepository(fhirEngine))
    PatientRepository.seedQuestionnaireAsync()
    PatientRepository.seedSamplePatientsAsync()
  }
}
