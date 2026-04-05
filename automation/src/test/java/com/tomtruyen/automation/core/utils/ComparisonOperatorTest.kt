package com.tomtruyen.automation.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ComparisonOperatorTest {
    @Test
    fun matches_withIntegers_usesExpectedComparisons() {
        assertTrue(ComparisonOperator.GREATER_THAN.matches(5, 4))
        assertTrue(ComparisonOperator.GREATER_THAN_OR_EQUAL.matches(4, 4))
        assertTrue(ComparisonOperator.LESS_THAN.matches(3, 4))
        assertTrue(ComparisonOperator.LESS_THAN_OR_EQUAL.matches(4, 4))
        assertTrue(ComparisonOperator.EQUAL.matches(4, 4))
        assertTrue(ComparisonOperator.NOT_EQUAL.matches(4, 5))

        assertFalse(ComparisonOperator.GREATER_THAN.matches(4, 4))
        assertFalse(ComparisonOperator.LESS_THAN.matches(4, 4))
        assertFalse(ComparisonOperator.EQUAL.matches(4, 5))
    }

    @Test
    fun matches_withFloats_usesExpectedComparisons() {
        assertTrue(ComparisonOperator.GREATER_THAN.matches(5.1f, 5.0f))
        assertTrue(ComparisonOperator.GREATER_THAN_OR_EQUAL.matches(5.0f, 5.0f))
        assertTrue(ComparisonOperator.LESS_THAN.matches(4.9f, 5.0f))
        assertTrue(ComparisonOperator.LESS_THAN_OR_EQUAL.matches(5.0f, 5.0f))
        assertTrue(ComparisonOperator.EQUAL.matches(5.0f, 5.0f))
        assertTrue(ComparisonOperator.NOT_EQUAL.matches(5.0f, 4.0f))

        assertFalse(ComparisonOperator.GREATER_THAN.matches(5.0f, 5.0f))
        assertFalse(ComparisonOperator.LESS_THAN.matches(5.0f, 5.0f))
        assertFalse(ComparisonOperator.EQUAL.matches(5.0f, 5.1f))
    }
}
