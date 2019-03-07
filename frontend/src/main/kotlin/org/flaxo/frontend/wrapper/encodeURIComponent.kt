package org.flaxo.frontend.wrapper

/**
 * Encodes URI components in string so it can be used as an HTTP request parameters.
 */
@JsName("encodeURIComponent")
external fun encodeURIComponent(uri: String): String
