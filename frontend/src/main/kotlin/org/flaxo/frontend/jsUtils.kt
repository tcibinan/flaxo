package org.flaxo.frontend

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import kotlin.browser.document

/**
 * Clicks on a button if there is one with the specified [id].
 */
fun clickOnButton(id: String) {
    document.getElementById(id)?.let { it as? HTMLButtonElement }?.click()
}

/**
 * Removes css [classes] from the DOM object with the specified [id].
 */
fun removeClasses(id: String, vararg classes: String) {
    document.getElementById(id)?.classList?.remove(*classes)
}

/**
 * Adds css [classes] to the DOM object with the specified [id].
 */
fun addClasses(id: String, vararg classes: String) {
    document.getElementById(id)?.classList?.add(*classes)
}

/**
 * Retrieves selector object value by the specified [id].
 */
fun selectValue(id: String): String? = document.getElementById(id)
        ?.let { it as? HTMLSelectElement }
        ?.value

/**
 * Retrieves input object value by the specified [id].
 */
fun inputValue(id: String): String? = document.getElementById(id)
        ?.let { it as? HTMLInputElement }
        ?.value

/**
 * Clears input object value by the specified [id].
 */
fun clearInputValue(id: String) {
    document.getElementById(id)
            ?.let { it as? HTMLInputElement }
            ?.value = ""
}

/**
 * Toggles checkbox object by the specified [id].
 */
fun toggleCheckbox(id: String): Boolean =
        document.getElementById(id)
                ?.let { it as? HTMLInputElement }
                ?.also { it.click() }
                ?.checked
                ?: false

/**
 * Retrieves checkbox object value by the specified [id].
 */
fun checkBoxValue(id: String): Boolean =
        document.getElementById(id)
                ?.let { it as? HTMLInputElement }
                ?.checked
                ?: false

private const val INVALID_INPUT_VALUE_CLASS = "is-invalid"

/**
 * Validates non-emptiness of an input object value with the specified [id].
 *
 * If the value is empty than it adds an additional css class [INVALID_INPUT_VALUE_CLASS] to the object.
 */
fun validatedInputValue(id: String): String? {
    removeClasses(id, INVALID_INPUT_VALUE_CLASS)
    val fieldValue = inputValue(id)?.takeIf { it.isNotBlank() }
    if (fieldValue == null) addClasses(id, INVALID_INPUT_VALUE_CLASS)
    return fieldValue
}
