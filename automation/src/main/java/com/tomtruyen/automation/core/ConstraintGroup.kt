package com.tomtruyen.automation.core

import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import kotlinx.serialization.Serializable

@Serializable
data class ConstraintGroup(val constraints: List<ConstraintConfig> = emptyList())

fun List<ConstraintGroup>.effectiveConstraintGroups(
    fallbackConstraints: List<ConstraintConfig>,
): List<ConstraintGroup> = filter { it.constraints.isNotEmpty() }.ifEmpty {
    fallbackConstraints.takeIf { it.isNotEmpty() }?.let { listOf(ConstraintGroup(it)) }.orEmpty()
}
