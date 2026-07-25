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
package icl.ohs.reference.feature.patient.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import icl.ohs.library.layout.GridListRenderer
import icl.ohs.library.layout.HorizontalListRenderer
import icl.ohs.library.layout.VerticalListRenderer
import icl.ohs.library.registry.ViewRegistry
import icl.ohs.library.registry.registerComponent
import icl.ohs.library.registry.registerLayout
import icl.ohs.mobile.generated.config.PatientCardConfig
import icl.ohs.mobile.generated.state.PatientSummaryState
import icl.ohs.mobile.generated.viewtype.ViewTypeCS

fun ViewRegistry.registerPatientList() {
  registerComponent<PatientSummaryState, PatientCardConfig>(
    ViewTypeCS.PatientCard,
    PatientCardRenderer(),
    PatientCardConfig(),
  )
  registerLayout<PatientSummaryState>(
    VerticalListRenderer.VIEW_TYPE,
    VerticalListRenderer(contentPadding = PaddingValues(16.dp), itemSpacing = 12.dp),
  )
  registerLayout<PatientSummaryState>(
    HorizontalListRenderer.VIEW_TYPE,
    HorizontalListRenderer(contentPadding = PaddingValues(16.dp), itemSpacing = 12.dp),
  )
  registerLayout<PatientSummaryState>(
    GridListRenderer.VIEW_TYPE,
    GridListRenderer(itemSpacing = 12.dp),
  )
}
