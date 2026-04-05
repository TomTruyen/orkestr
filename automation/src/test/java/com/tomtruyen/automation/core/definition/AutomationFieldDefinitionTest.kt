package com.tomtruyen.automation.core.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.config.AutomationConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationFieldDefinitionTest {
    private val resolver = object : AutomationTextResolver {
        override fun resolve(stringRes: Int, formatArgs: List<Any>): String = buildString {
            append("res:")
            append(stringRes)
            if (formatArgs.isNotEmpty()) {
                append(":")
                append(formatArgs.joinToString())
            }
        }
    }

    @Test
    fun validateInput_whenRequiredAndBlank_returnsRequiredError() {
        val field = object : AutomationFieldDefinition(
            id = "field",
            labelRes = 10,
            type = AutomationFieldType.TEXT,
            descriptionRes = 11,
        ) {
            override fun readValue(config: AutomationConfig<*>?) = ""
            override fun updateValue(config: AutomationConfig<*>?, input: String): AutomationConfig<*> =
                TestFieldConfig(
                    input,
                )
        }

        val errors = field.validateInput("", resolver)

        assertEquals(
            listOf("res:${R.string.automation_definition_error_required}:res:10"),
            errors,
        )
    }

    @Test
    fun validateInput_whenNumberIsInvalid_returnsNumberError() {
        val field = object : AutomationFieldDefinition(
            id = "field",
            labelRes = 10,
            type = AutomationFieldType.NUMBER,
            descriptionRes = 11,
            required = false,
        ) {
            override fun readValue(config: AutomationConfig<*>?) = ""
            override fun updateValue(config: AutomationConfig<*>?, input: String): AutomationConfig<*> =
                TestFieldConfig(
                    input,
                )
        }

        val errors = field.validateInput("abc", resolver)

        assertEquals(
            listOf("res:${R.string.automation_definition_error_number}:res:10"),
            errors,
        )
    }

    @Test
    fun typedFieldDefinition_usesDefaultConfigForReadAndUpdateWhenTypeDoesNotMatch() {
        val field = typedFieldDefinition()

        assertEquals("fallback", field.readValue(OtherFieldConfig))
        assertEquals(TestFieldConfig("updated"), field.updateValue(OtherFieldConfig, "updated"))
    }

    @Test
    fun typedFieldDefinition_validateInput_combinesBaseAndCustomValidation() {
        val field = TypedAutomationFieldDefinition(
            configClass = TestFieldConfig::class,
            defaultConfig = TestFieldConfig("fallback"),
            id = "field",
            labelRes = 10,
            type = AutomationFieldType.TEXT,
            descriptionRes = 11,
            defaultValue = "fallback",
            reader = { it.value },
            updater = { config, input -> config.copy(value = input) },
            inputValidator = { input, _ ->
                if (input == "fallback") listOf("custom-error") else emptyList()
            },
        )

        val errors = field.validateInput("", resolver)

        assertEquals(listOf("custom-error"), errors)
    }

    private fun typedFieldDefinition() = TypedAutomationFieldDefinition(
        configClass = TestFieldConfig::class,
        defaultConfig = TestFieldConfig("fallback"),
        id = "field",
        labelRes = 10,
        type = AutomationFieldType.TEXT,
        descriptionRes = 11,
        defaultValue = "fallback",
        reader = { it.value },
        updater = { config, input -> config.copy(value = input) },
    )

    private data class TestFieldConfig(val value: String = "default") : AutomationConfig<TestFieldType> {
        override val type: TestFieldType = TestFieldType.FIELD
        override val category: AutomationCategory = AutomationCategory.UTILITY
    }

    private data object OtherFieldConfig : AutomationConfig<TestFieldType> {
        override val type: TestFieldType = TestFieldType.FIELD
        override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    }

    private enum class TestFieldType {
        FIELD,
    }
}
