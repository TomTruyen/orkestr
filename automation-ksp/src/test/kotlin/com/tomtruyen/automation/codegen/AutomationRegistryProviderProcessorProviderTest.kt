package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AutomationRegistryProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesRegistryProviderFile() {
        val triggerType = support.requiredType()
        val constraintType = support.requiredType()
        val actionType = support.requiredType()
        support.stubRequiredType(TRIGGER_DEFINITION_TYPE, triggerType)
        support.stubRequiredType(CONSTRAINT_DEFINITION_TYPE, constraintType)
        support.stubRequiredType(ACTION_DEFINITION_TYPE, actionType)
        support.setSymbols(
            GenerateTriggerDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.TriggerDefinition", triggerType),
        )
        support.setSymbols(
            GenerateConstraintDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.ConstraintDefinition", constraintType),
        )
        support.setSymbols(
            GenerateActionDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.ActionDefinition", actionType),
        )

        val processor = AutomationRegistryProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_REGISTRY_PROVIDER)
        assertTrue(generated.contains("object GeneratedAutomationRegistryProvider"))
        assertTrue(generated.contains("GeneratedTriggerProvider.definitions"))
        assertTrue(generated.contains("GeneratedConstraintProvider.definitions"))
        assertTrue(generated.contains("GeneratedActionProvider.definitions"))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_REGISTRY_PROVIDER, any())
        }
    }
}
