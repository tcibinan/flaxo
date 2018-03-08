package com.tcibinan.flaxo.model

/**
 * Entity already exists exception.
 */
class EntityAlreadyExistsException(entity: String) : ModelException(entity + " already exists")

/**
 * Entity not found exception.
 */
class EntityNotFound(entity: String) : ModelException(entity + " hasn't been found")

/**
 * Entity field not initialised exception.
 */
class EntityFieldIsAbsent(entity: String, field: String) : ModelException("Entity $entity doesn't have $field initialized")

/**
 * Base model exception class.
 */
open class ModelException(message: String) : RuntimeException(message)