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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import icl.ohs.reference.data.repository.QuestionnaireForm

/**
 * "Add Clinical Data" form, rendered from the bundled [QuestionnaireForm] (its `category` item's
 * `choice` options drive the type picker below; its `display`/`date` item text becomes the field
 * labels) rather than hardcoded, so the fields shown always match what's actually seeded in the
 * engine.
 */
@Composable
fun AddClinicalDataDialog(
  form: QuestionnaireForm,
  onDismiss: () -> Unit,
  onSubmit: (category: String, display: String, date: String?) -> Unit,
) {
  val categoryItem = form.items.firstOrNull { it.type == "choice" }
  val displayItem = form.items.firstOrNull { it.linkId == "display" }
  val dateItem = form.items.firstOrNull { it.linkId == "date" }
  val options = categoryItem?.options.orEmpty()

  var category by remember { mutableStateOf(options.firstOrNull().orEmpty()) }
  var display by remember { mutableStateOf("") }
  var date by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(form.title) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (options.isNotEmpty()) {
          Column {
            Text(categoryItem?.text ?: "Category", style = MaterialTheme.typography.labelLarge)
            Row(
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              options.forEach { option ->
                val selected = option == category
                Surface(
                  shape = RoundedCornerShape(50),
                  color =
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                  modifier =
                    Modifier.selectable(selected = selected, onClick = { category = option }),
                ) {
                  Text(
                    text = option,
                    style = MaterialTheme.typography.labelMedium,
                    color =
                      if (selected) MaterialTheme.colorScheme.onPrimary
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  )
                }
              }
            }
          }
        }
        OutlinedTextField(
          value = display,
          onValueChange = { display = it },
          label = { Text(displayItem?.text ?: "Description") },
          modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text(dateItem?.text ?: "Date") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = {
      TextButton(
        onClick = { onSubmit(category, display, date.takeIf { it.isNotBlank() }) },
        enabled = category.isNotBlank() && display.isNotBlank(),
      ) {
        Text("Save")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}
