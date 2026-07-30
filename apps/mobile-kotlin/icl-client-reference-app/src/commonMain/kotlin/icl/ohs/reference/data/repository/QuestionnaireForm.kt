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

import dev.ohs.fhir.model.r4.Resource
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * A single `Questionnaire.item`, reduced to just the fields the "Add Clinical Data" form needs to
 * render itself: [linkId], the prompt [text], the item [type] (`choice`, `string`, `date`, …), and
 * any `answerOption` choices.
 */
data class QuestionnaireFormItem(
  val linkId: String,
  val text: String,
  val type: String,
  val options: List<String> = emptyList(),
)

/** The bundled Questionnaire, reduced to what the form needs: a [title] and its [items]. */
data class QuestionnaireForm(val title: String, val items: List<QuestionnaireFormItem>)

/**
 * Parses a [Resource] known to be a `Questionnaire` into the plain [QuestionnaireForm] shape.
 *
 * This walks the resource's raw JSON rather than going through typed `Questionnaire`/
 * `QuestionnaireItem` model classes - the form only needs a handful of primitive fields, and raw
 * JSON access for exactly this kind of thing is already the established pattern in this module (see
 * [FhirEngineRepository]'s reference rewriting).
 */
fun parseQuestionnaireForm(resource: Resource): QuestionnaireForm {
  val obj = FhirJson.instance.encodeToJsonElement(Resource.serializer(), resource).jsonObject
  val title = obj["title"]?.jsonPrimitive?.contentOrNull ?: "Questionnaire"
  val itemArray = obj["item"] as? JsonArray ?: JsonArray(emptyList())
  val items = itemArray.mapNotNull { (it as? JsonObject)?.let(::parseFormItem) }
  return QuestionnaireForm(title, items)
}

private fun parseFormItem(item: JsonObject): QuestionnaireFormItem? {
  val linkId = item["linkId"]?.jsonPrimitive?.contentOrNull ?: return null
  val text = item["text"]?.jsonPrimitive?.contentOrNull ?: linkId
  val type = item["type"]?.jsonPrimitive?.contentOrNull ?: "string"
  val answerOptions = item["answerOption"] as? JsonArray ?: JsonArray(emptyList())
  val options =
    answerOptions.mapNotNull { option ->
      (option as? JsonObject)?.get("valueString")?.jsonPrimitive?.contentOrNull
    }
  return QuestionnaireFormItem(linkId, text, type, options)
}

/**
 * Builds the FHIR resource a completed "Add Clinical Data" form describes: [category] selects the
 * resource type (matching one of the Questionnaire's `category` answerOptions), [display] becomes
 * its code/medication/vaccine display text, and [date] (if provided) becomes its onset/occurrence
 * date. The new resource has no `id` - [FhirRepository.upsert] assigns one.
 */
fun buildClinicalDataResource(
  patientId: String,
  category: String,
  display: String,
  date: String?,
): Resource {
  val element =
    when (category) {
      "Allergy" ->
        buildJsonObject {
          put("resourceType", "AllergyIntolerance")
          putJsonObject("patient") { put("reference", "Patient/$patientId") }
          putCodeableConcept("code", display)
          putClinicalStatus("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical")
        }
      "Medication" ->
        buildJsonObject {
          put("resourceType", "MedicationRequest")
          put("status", "active")
          put("intent", "order")
          putJsonObject("subject") { put("reference", "Patient/$patientId") }
          putCodeableConcept("medicationCodeableConcept", display)
        }
      "Condition" ->
        buildJsonObject {
          put("resourceType", "Condition")
          putJsonObject("subject") { put("reference", "Patient/$patientId") }
          putCodeableConcept("code", display)
          putClinicalStatus("http://terminology.hl7.org/CodeSystem/condition-clinical")
          date?.let { put("onsetDateTime", it) }
        }
      "Immunization" ->
        buildJsonObject {
          put("resourceType", "Immunization")
          put("status", "completed")
          putCodeableConcept("vaccineCode", display)
          putJsonObject("patient") { put("reference", "Patient/$patientId") }
          date?.let { put("occurrenceDateTime", it) }
        }
      else -> error("Unknown clinical data category: $category")
    }
  return FhirJson.instance.decodeFromJsonElement(Resource.serializer(), element)
}

private fun JsonObjectBuilder.putCodeableConcept(field: String, display: String) {
  putJsonObject(field) { putJsonArray("coding") { addJsonObject { put("display", display) } } }
}

private fun JsonObjectBuilder.putClinicalStatus(system: String) {
  putJsonObject("clinicalStatus") {
    putJsonArray("coding") {
      addJsonObject {
        put("system", system)
        put("code", "active")
      }
    }
  }
}
