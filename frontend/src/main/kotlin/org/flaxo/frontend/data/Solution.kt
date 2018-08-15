package org.flaxo.frontend.data

class Solution(val task: String,
               val student: String,
               val score: Int?,
               val commits: List<Commit>,
               val buildReports: List<BuildReport>,
               val codeStyleReports: List<CodeStyleReport>)