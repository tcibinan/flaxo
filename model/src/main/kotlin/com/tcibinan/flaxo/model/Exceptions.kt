package com.tcibinan.flaxo.model

class EntityAlreadyExistsException(val entity: String) : Exception(entity + "already exists")

class EntityNotFound(entity: String) : Exception(entity + "hasn't been found")