package com.tcibinan.flaxo.core

interface FlaxoException
class EntityAlreadyExistsException(val entity: String) : Exception(entity + "already exists"), FlaxoException