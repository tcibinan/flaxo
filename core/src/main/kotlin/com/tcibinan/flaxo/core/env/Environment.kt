package com.tcibinan.flaxo.core.env

interface Environment {
    fun getFiles(): Set<File>
}