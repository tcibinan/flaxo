package com.tcibinan.flaxo.core.model

interface DataObject<T> {
    fun toEntity(): T
}