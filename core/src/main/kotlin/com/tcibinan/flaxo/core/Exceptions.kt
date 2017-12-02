package com.tcibinan.flaxo.core

class EntityAlreadyExistsException(override val message: String) : FlaxoException(message)
open class FlaxoException(message: String) : Exception(message)