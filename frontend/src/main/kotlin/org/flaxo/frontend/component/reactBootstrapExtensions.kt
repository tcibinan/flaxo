package org.flaxo.frontend.component

import kotlinx.html.Tag
import react.dom.RDOMBuilder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private abstract class ReactProperty<TAG : Tag, PROPERTY_TYPE>(private val name: String)
    : ReadWriteProperty<RDOMBuilder<TAG>, PROPERTY_TYPE> {

    override fun getValue(thisRef: RDOMBuilder<TAG>, property: KProperty<*>): PROPERTY_TYPE =
            thisRef.attrs.attributes[name].toPropertyType()

    override fun setValue(thisRef: RDOMBuilder<TAG>, property: KProperty<*>, value: PROPERTY_TYPE) {
        thisRef.attrs.attributes[name] = value?.toString() ?: ""
    }

    abstract fun String?.toPropertyType(): PROPERTY_TYPE

}

private class StringReactProperty<TAG : Tag>(name: String) : ReactProperty<TAG, String>(name) {
    override fun String?.toPropertyType(): String = this ?: ""
}

private class BooleanReactProperty<TAG : Tag>(name: String) : ReactProperty<TAG, Boolean>(name) {
    override fun String?.toPropertyType(): Boolean = this?.toBoolean() ?: false
}

var <T : Tag> RDOMBuilder<T>.dataDismiss: String by StringReactProperty("data-dismiss")
var <T : Tag> RDOMBuilder<T>.dataToggle: String by StringReactProperty("data-toggle")
var <T : Tag> RDOMBuilder<T>.dataTarget: String by StringReactProperty("data-target")
var <T : Tag> RDOMBuilder<T>.ariaLabel: String by StringReactProperty("aria-label")
var <T : Tag> RDOMBuilder<T>.ariaLabelledBy: String by StringReactProperty("aria-labelledby")
var <T : Tag> RDOMBuilder<T>.ariaDescribedBy: String by StringReactProperty("aria-describedby")
var <T : Tag> RDOMBuilder<T>.ariaHidden: Boolean by BooleanReactProperty("aria-hidden")
var <T : Tag> RDOMBuilder<T>.defaultValue: String by StringReactProperty("defaultValue")
