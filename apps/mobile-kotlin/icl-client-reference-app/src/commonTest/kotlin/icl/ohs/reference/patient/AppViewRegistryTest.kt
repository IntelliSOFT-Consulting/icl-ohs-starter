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
package icl.ohs.reference.patient

import icl.ohs.library.layout.GridListRenderer
import icl.ohs.library.layout.HorizontalListRenderer
import icl.ohs.library.layout.VerticalListRenderer
import icl.ohs.library.registry.componentRenderer
import icl.ohs.library.registry.layoutRenderer
import icl.ohs.reference.buildAppViewRegistry
import kotlin.test.Test

class AppViewRegistryTest {

  @Test
  fun allRequiredRenderersAreRegistered() {
    val registry = buildAppViewRegistry()

    // Patient list — component + every layout
    registry.componentRenderer<PatientSummaryState>(ViewTypeCS.PatientCard)
    registry.layoutRenderer<PatientSummaryState>(VerticalListRenderer.VIEW_TYPE)
    registry.layoutRenderer<PatientSummaryState>(HorizontalListRenderer.VIEW_TYPE)
    registry.layoutRenderer<PatientSummaryState>(GridListRenderer.VIEW_TYPE)

    // Patient IPS profile — all IG-authored item types
    registry.componentRenderer<PatientSummaryState>(ViewTypeCS.PatientHeader)
    registry.componentRenderer<PatientAllergyState>(ViewTypeCS.AllergyItem)
    registry.componentRenderer<PatientMedicationState>(ViewTypeCS.MedicationItem)
    registry.componentRenderer<PatientConditionState>(ViewTypeCS.ConditionItem)
    registry.componentRenderer<PatientImmunizationState>(ViewTypeCS.ImmunizationItem)
  }
}
