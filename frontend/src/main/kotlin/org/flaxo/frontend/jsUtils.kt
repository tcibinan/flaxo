package org.flaxo.frontend

import org.flaxo.frontend.component.RegistrationModal
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

fun clickOnButton(id: String) {
    document.getElementById(id)?.let { it as? HTMLButtonElement }?.click()
}

fun removeClasses(id: String, vararg classes: String) {
    document.getElementById(id)?.classList?.remove(*classes)
}

fun addClasses(id: String, vararg classes: String) {
    document.getElementById(id)?.classList?.add(*classes)
}

private const val INVALID_INPUT_VALUE_CLASS = "is-invalid"

fun validateFormInputField(id: String): String? {
    removeClasses(id, INVALID_INPUT_VALUE_CLASS)
    val fieldValue = document.getElementById(id)
            ?.let { it as? HTMLInputElement }
            ?.value
            ?.takeIf { it.isNotBlank() }
    if (fieldValue == null) addClasses(id, INVALID_INPUT_VALUE_CLASS)
    return fieldValue
}
