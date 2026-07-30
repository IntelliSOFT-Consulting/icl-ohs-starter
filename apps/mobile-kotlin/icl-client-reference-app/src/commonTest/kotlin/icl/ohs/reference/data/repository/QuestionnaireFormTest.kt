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
import dev.ohs.fhir.model.r4.Condition
import dev.ohs.fhir.model.r4.Immunization
import dev.ohs.fhir.model.r4.MedicationRequest
import dev.ohs.fhir.model.r4.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mirrors the bundled `Questionnaire-add-clinical-data.json` asset content - kept as a literal here
 * (rather than reading the real asset via Compose resources, which commonTest can't do) purely to
 * exercise [parseQuestionnaireForm]'s JSON walking.
 */
private val ADD_CLINICAL_DATA_QUESTIONNAIRE_JSON =
  """
    {
      "resourceType": "Questionnaire",
      "id": "add-clinical-data",
      "status": "active",
      "title": "Add Clinical Data",
      "item": [
        {
          "linkId": "category",
          "text": "What would you like to record?",
          "type": "choice",
          "answerOption": [
            {"valueString": "Allergy"},
            {"valueString": "Medication"},
            {"valueString": "Condition"},
            {"valueString": "Immunization"}
          ]
        },
        {"linkId": "display", "text": "Description", "type": "string"},
        {"linkId": "date", "text": "Date (optional, YYYY-MM-DD)", "type": "date"}
      ]
    }
  """
    .trimIndent()

class QuestionnaireFormTest {

  private fun questionnaireResource(): Resource =
    FhirJson.instance.decodeFromString(Resource.serializer(), ADD_CLINICAL_DATA_QUESTIONNAIRE_JSON)

  @Test
  fun parseQuestionnaireForm_readsTitleAndItems() {
    val form = parseQuestionnaireForm(questionnaireResource())

    assertEquals("Add Clinical Data", form.title)
    assertEquals(listOf("category", "display", "date"), form.items.map { it.linkId })

    val category = form.items.first { it.linkId == "category" }
    assertEquals("choice", category.type)
    assertEquals(listOf("Allergy", "Medication", "Condition", "Immunization"), category.options)

    val display = form.items.first { it.linkId == "display" }
    assertEquals("Description", display.text)
    assertTrue(display.options.isEmpty())
  }

  @Test
  fun buildClinicalDataResource_allergy() {
    val resource = buildClinicalDataResource("p1", "Allergy", "Penicillin", date = null)
    val allergy = resource as AllergyIntolerance

    assertEquals("Patient/p1", allergy.patient.reference?.value)
    assertEquals("Penicillin", allergy.code?.coding?.firstOrNull()?.display?.value)
  }

  @Test
  fun buildClinicalDataResource_medication() {
    val resource = buildClinicalDataResource("p1", "Medication", "Amoxicillin", date = null)
    val medication = resource as MedicationRequest

    assertEquals("Patient/p1", medication.subject.reference?.value)
    assertEquals(
      "Amoxicillin",
      medication.medicationCodeableConcept?.coding?.firstOrNull()?.display?.value,
    )
  }

  @Test
  fun buildClinicalDataResource_condition_withDate() {
    val resource = buildClinicalDataResource("p1", "Condition", "Asthma", date = "2020-01-01")
    val condition = resource as Condition

    assertEquals("Patient/p1", condition.subject.reference?.value)
    assertEquals("Asthma", condition.code?.coding?.firstOrNull()?.display?.value)
    assertEquals("2020-01-01", condition.onsetDateTime?.value.toString())
  }

  @Test
  fun buildClinicalDataResource_immunization_withoutDate() {
    val resource = buildClinicalDataResource("p1", "Immunization", "BCG vaccine", date = null)
    val immunization = resource as Immunization

    assertEquals("Patient/p1", immunization.patient.reference?.value)
    assertEquals("BCG vaccine", immunization.vaccineCode.coding?.firstOrNull()?.display?.value)
    assertNull(immunization.occurrenceDateTime)
  }
}
