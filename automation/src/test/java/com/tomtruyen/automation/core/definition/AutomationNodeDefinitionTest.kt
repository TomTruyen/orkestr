package com.tomtruyen.automation.core.definition

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.permission.NotificationPolicyAccessPermission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class AutomationNodeDefinitionTest {
    private val resolver = object : AutomationTextResolver {
        override fun resolve(stringRes: Int, formatArgs: List<Any>): String =
            "res:$stringRes" + if (formatArgs.isEmpty()) "" else ":" + formatArgs.joinToString()
    }

    @Test
    fun defaultProperties_areDerivedFromDefaultConfig() {
        assertEquals(TestNodeType.NODE.name, TestNodeDefinition.key)
        assertEquals(TestNodeType.NODE, TestNodeDefinition.type)
        assertEquals(AutomationCategory.UTILITY, TestNodeDefinition.category)
        assertEquals(listOf(NotificationPolicyAccessPermission), TestNodeDefinition.requiredPermissions)
        assertEquals(NotificationPolicyAccessPermission.minSdk, TestNodeDefinition.requiredMinSdk)
        assertEquals(TestNodeConfig(), TestNodeDefinition.initialConfig())
    }

    @Test
    fun cast_returnsTypedConfigForMatchingInstance_andNullOtherwise() {
        assertEquals(TestNodeConfig("value"), TestNodeDefinition.cast(TestNodeConfig("value")))
        assertNull(TestNodeDefinition.cast(OtherNodeConfig))
    }

    @Test
    fun updateFieldAny_updatesMatchingField_andFallsBackForUnknownFields() {
        assertEquals(
            TestNodeConfig("updated"),
            TestNodeDefinition.updateFieldAny(null, "value", "updated"),
        )
        assertEquals(
            TestNodeConfig("existing"),
            TestNodeDefinition.updateFieldAny(TestNodeConfig("existing"), "missing", "updated"),
        )
        assertEquals(
            TestNodeConfig(),
            TestNodeDefinition.updateFieldAny(OtherNodeConfig, "missing", "updated"),
        )
    }

    @Test
    fun validateAndSummarizeAny_useTypedConfigOrDefaultConfig() {
        assertEquals(emptyList<String>(), TestNodeDefinition.validate(TestNodeConfig("valid"), resolver))
        assertEquals(listOf("invalid"), TestNodeDefinition.validateAny(TestNodeConfig("invalid"), resolver))
        assertEquals(emptyList<String>(), TestNodeDefinition.validateAny(OtherNodeConfig, resolver))
        assertEquals("summary:valid", TestNodeDefinition.summarizeAny(TestNodeConfig("valid"), resolver))
        assertEquals("summary:default", TestNodeDefinition.summarizeAny(OtherNodeConfig, resolver))
    }

    private object TestNodeDefinition : AutomationNodeDefinition<TestNodeConfig, TestNodeType> {
        override val configClass = TestNodeConfig::class
        override val defaultConfig = TestNodeConfig()
        override val titleRes: Int = 1
        override val descriptionRes: Int = 2
        override val fields: List<AutomationFieldDefinition> = listOf(
            TypedAutomationFieldDefinition(
                configClass = TestNodeConfig::class,
                defaultConfig = TestNodeConfig(),
                id = "value",
                labelRes = 3,
                type = AutomationFieldType.TEXT,
                descriptionRes = 4,
                reader = { it.value },
                updater = { config, input -> config.copy(value = input) },
                inputValidator = { input, _ -> if (input == "invalid") listOf("invalid") else emptyList() },
            ),
        )

        override fun summarize(config: TestNodeConfig, resolver: AutomationTextResolver): String =
            "summary:${config.value}"
    }

    private data class TestNodeConfig(val value: String = "default") : AutomationConfig<TestNodeType> {
        override val type: TestNodeType = TestNodeType.NODE
        override val category: AutomationCategory = AutomationCategory.UTILITY
        override val requiredPermissions = listOf(NotificationPolicyAccessPermission)
    }

    private data object OtherNodeConfig : AutomationConfig<TestNodeType> {
        override val type: TestNodeType = TestNodeType.NODE
        override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    }

    private enum class TestNodeType {
        NODE,
    }
}
