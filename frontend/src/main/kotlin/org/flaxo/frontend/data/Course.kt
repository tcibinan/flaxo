package org.flaxo.frontend.data

import kotlin.js.Date

class Course(val name: String,
             val description: String?,
             val createdDate: Date,
             val language: String,
             val testingLanguage: String,
             val testingFramework: String,
             val url: String,
             val state: CourseState,
             val user: User,
             val students: List<String>,
             val tasks: List<String>)