package com.tcibinan.flaxo.core

class EntityAlreadyExistsException(message: String) : FlaxoException(message)
open class FlaxoException(message: String) : Exception(message)