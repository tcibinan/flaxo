package com.tcibinan.flaxo.model

import kotlin.reflect.KClass

/**
 * Base model exception class.
 */
open class ModelException(message: String) : RuntimeException(message)

/**
 * Entity already exists exception.
 */
class EntityAlreadyExistsException(entity: String)
    : ModelException("$entity already exists")

/**
 * Entity not found exception.
 */
class EntityNotFound(entity: String)
    : ModelException("$entity hasn't been found")