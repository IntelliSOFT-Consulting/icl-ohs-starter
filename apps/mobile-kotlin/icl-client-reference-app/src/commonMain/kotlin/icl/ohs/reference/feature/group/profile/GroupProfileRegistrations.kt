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
package icl.ohs.reference.feature.group.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import icl.ohs.library.registry.ViewRegistry
import icl.ohs.library.registry.registerComponent
import icl.ohs.library.registry.registerLayout
import icl.ohs.mobile.generated.config.GroupHeaderConfig
import icl.ohs.mobile.generated.config.MemberItemConfig
import icl.ohs.mobile.generated.config.SectionCardConfig
import icl.ohs.mobile.generated.state.GroupHeaderState
import icl.ohs.mobile.generated.state.GroupMemberState
import icl.ohs.mobile.generated.viewtype.ViewTypeCS
import icl.ohs.reference.feature.component.common.SectionCardLayoutRenderer

fun ViewRegistry.registerGroupProfile() {
  registerComponent<GroupHeaderState, GroupHeaderConfig>(
    ViewTypeCS.GroupHeader,
    GroupHeaderRenderer(),
    GroupHeaderConfig(),
  )
  registerComponent<GroupMemberState, MemberItemConfig>(
    ViewTypeCS.MemberItem,
    MemberItemRenderer(),
    MemberItemConfig(),
  )
  registerLayout<GroupMemberState>(
    ViewTypeCS.SectionCard,
    SectionCardLayoutRenderer(
      title = "Members",
      icon = Icons.Default.Person,
      config = SectionCardConfig(collapsible = true),
    ),
  )
}
