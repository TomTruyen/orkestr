package com.tomtruyen.orkestr.features.automation.component

import com.tomtruyen.automation.generated.GeneratedActionProvider
import com.tomtruyen.automation.generated.GeneratedConstraintProvider
import com.tomtruyen.automation.generated.GeneratedTriggerProvider
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationGeneratedDefinitionPreviewCatalogTest {
    @Test
    fun triggerPreviewCatalog_matchesGeneratedProvider() {
        assertEquals(
            GeneratedTriggerProvider.definitions.mapTo(linkedSetOf()) { it.type },
            AutomationGeneratedDefinitionPreviewCatalog.triggerTypes,
        )
    }

    @Test
    fun constraintPreviewCatalog_matchesGeneratedProvider() {
        assertEquals(
            GeneratedConstraintProvider.definitions.mapTo(linkedSetOf()) { it.type },
            AutomationGeneratedDefinitionPreviewCatalog.constraintTypes,
        )
    }

    @Test
    fun actionPreviewCatalog_matchesGeneratedProvider() {
        assertEquals(
            GeneratedActionProvider.definitions.mapTo(linkedSetOf()) { it.type },
            AutomationGeneratedDefinitionPreviewCatalog.actionTypes,
        )
    }
}
