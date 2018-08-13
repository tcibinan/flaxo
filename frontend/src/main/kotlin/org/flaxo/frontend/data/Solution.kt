package org.flaxo.frontend.data

class Solution(val task: String,
               val student: String,
               val score: Int,
               val commits: List<CommitModel>,
               val buildReports: List<BuildReport>,
               val codestyleReports: List<CodeStyleReport>)