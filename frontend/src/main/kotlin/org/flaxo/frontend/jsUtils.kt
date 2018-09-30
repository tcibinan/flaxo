package org.flaxo.frontend

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
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

fun selectValue(id: String): String? = document.getElementById(id)
        ?.let { it as? HTMLSelectElement }
        ?.value

fun inputValue(id: String): String? = document.getElementById(id)
        ?.let { it as? HTMLInputElement }
        ?.value

private const val INVALID_INPUT_VALUE_CLASS = "is-invalid"

fun validatedInputValue(id: String): String? {
    removeClasses(id, INVALID_INPUT_VALUE_CLASS)
    val fieldValue = inputValue(id)?.takeIf { it.isNotBlank() }
    if (fieldValue == null) addClasses(id, INVALID_INPUT_VALUE_CLASS)
    return fieldValue
}
