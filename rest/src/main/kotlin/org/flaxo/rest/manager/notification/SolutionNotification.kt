package org.flaxo.rest.manager.notification

import org.flaxo.model.data.Solution

data class SolutionNotification(val solution: Solution, val message: String)
