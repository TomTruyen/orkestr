package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.ui.automation.R
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationRuleEditorPackageChangedDefinitionHelpersTest {
    @Test
    fun customNavigationEventFor_packageChangedTrigger_opensAppPicker() {
        val event = packageChangedRegistry.customNavigationEventFor(
            section = RuleSection.TRIGGERS,
            typeKey = TriggerType.PACKAGE_CHANGED.name,
        )

        assertEquals(AutomationEditorEvent.NavigateToPackageChangedTriggerAppSelection, event)
    }

    @Test
    fun customConfigurationButtonLabel_forPackageChangedTrigger_usesAppPickerLabel() {
        val labelRes = customConfigurationButtonLabelRes(
            section = RuleSection.TRIGGERS,
            typeKey = TriggerType.PACKAGE_CHANGED.name,
        )

        assertEquals(R.string.automation_action_open_app_picker_flow, labelRes)
    }
}
