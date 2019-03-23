package org.flaxo.rest.manager.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Http response.
 */
class Response<T> : ResponseEntity<Any> {
    constructor(body: T?, status: HttpStatus): super(body, status)
    constructor(message: String, status: HttpStatus): super(message, status)
}
