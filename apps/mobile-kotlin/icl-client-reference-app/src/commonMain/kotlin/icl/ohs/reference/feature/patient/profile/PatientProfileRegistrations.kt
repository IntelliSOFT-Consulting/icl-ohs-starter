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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import icl.ohs.library.registry.ViewRegistry
import icl.ohs.library.registry.registerComponent
import icl.ohs.library.registry.registerLayout
import icl.ohs.mobile.generated.config.AllergyItemConfig
import icl.ohs.mobile.generated.config.AllergyReactionItemConfig
import icl.ohs.mobile.generated.config.ConditionItemConfig
import icl.ohs.mobile.generated.config.ContactItemConfig
import icl.ohs.mobile.generated.config.ImmunizationItemConfig
import icl.ohs.mobile.generated.config.MedicationItemConfig
import icl.ohs.mobile.generated.config.PatientHeaderConfig
import icl.ohs.mobile.generated.config.SectionCardConfig
import icl.ohs.mobile.generated.config.TelecomItemConfig
import icl.ohs.mobile.generated.state.AllergyReactionState
import icl.ohs.mobile.generated.state.PatientAllergyState
import icl.ohs.mobile.generated.state.PatientConditionState
import icl.ohs.mobile.generated.state.PatientContactState
import icl.ohs.mobile.generated.state.PatientImmunizationState
import icl.ohs.mobile.generated.state.PatientMedicationState
import icl.ohs.mobile.generated.state.PatientSummaryState
import icl.ohs.mobile.generated.state.PatientTelecomState
import icl.ohs.mobile.generated.viewtype.ViewTypeCS
import icl.ohs.reference.feature.component.common.SectionCardLayoutRenderer

fun ViewRegistry.registerPatientProfile() {
  registerComponent<PatientSummaryState, PatientHeaderConfig>(
    ViewTypeCS.PatientHeader,
    PatientHeaderRenderer(),
    PatientHeaderConfig(),
  )
  registerComponent<PatientAllergyState, AllergyItemConfig>(
    ViewTypeCS.AllergyItem,
    AllergyItemRenderer(),
    AllergyItemConfig(),
  )
  registerComponent<AllergyReactionState, AllergyReactionItemConfig>(
    ViewTypeCS.AllergyReactionItem,
    AllergyReactionItemRenderer(),
    AllergyReactionItemConfig(),
  )
  registerLayout<AllergyReactionState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Allergy Reactions",
      icon = Icons.Default.Warning,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerComponent<PatientMedicationState, MedicationItemConfig>(
    ViewTypeCS.MedicationItem,
    MedicationItemRenderer(),
    MedicationItemConfig(),
  )
  registerComponent<PatientConditionState, ConditionItemConfig>(
    ViewTypeCS.ConditionItem,
    ConditionItemRenderer(),
    ConditionItemConfig(),
  )
  registerComponent<PatientImmunizationState, ImmunizationItemConfig>(
    ViewTypeCS.ImmunizationItem,
    ImmunizationItemRenderer(),
    ImmunizationItemConfig(),
  )
  registerLayout<PatientAllergyState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Allergies",
      icon = Icons.Default.Warning,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerLayout<PatientMedicationState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Medications",
      icon = Icons.Default.Favorite,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerLayout<PatientConditionState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Conditions",
      icon = Icons.Default.Info,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerLayout<PatientImmunizationState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Immunizations",
      icon = Icons.Default.CheckCircle,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerComponent<PatientContactState, ContactItemConfig>(
    ViewTypeCS.ContactItem,
    ContactItemRenderer(),
    ContactItemConfig(),
  )
  registerLayout<PatientContactState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Contacts",
      icon = Icons.Default.Person,
      config = SectionCardConfig(collapsible = true),
    ),
  )
  registerComponent<PatientTelecomState, TelecomItemConfig>(
    ViewTypeCS.TelecomItem,
    TelecomItemRenderer(),
    TelecomItemConfig(),
  )
  registerLayout<PatientTelecomState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Contact Information",
      icon = Icons.Default.Phone,
      config = SectionCardConfig(collapsible = false),
    ),
  )
}
