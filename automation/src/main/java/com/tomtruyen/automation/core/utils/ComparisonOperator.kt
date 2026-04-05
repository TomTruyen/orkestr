package com.tomtruyen.automation.core.utils

import kotlinx.serialization.Serializable

@Serializable
enum class ComparisonOperator {
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    EQUAL,
    NOT_EQUAL,
    ;

    fun matches(left: Int, right: Int): Boolean = when (this) {
        GREATER_THAN -> left > right
        GREATER_THAN_OR_EQUAL -> left >= right
        LESS_THAN -> left < right
        LESS_THAN_OR_EQUAL -> left <= right
        EQUAL -> left == right
        NOT_EQUAL -> left != right
    }

    fun matches(left: Float, right: Float): Boolean = when (this) {
        GREATER_THAN -> left > right
        GREATER_THAN_OR_EQUAL -> left >= right
        LESS_THAN -> left < right
        LESS_THAN_OR_EQUAL -> left <= right
        EQUAL -> left == right
        NOT_EQUAL -> left != right
    }
}
