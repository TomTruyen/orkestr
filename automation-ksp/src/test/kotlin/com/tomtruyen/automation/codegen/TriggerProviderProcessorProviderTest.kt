package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class TriggerProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesTriggerProviderFile() {
        val definitionType = support.requiredType()
        val delegateType = support.requiredType()
        support.stubRequiredType(TRIGGER_DEFINITION_TYPE, definitionType)
        support.stubRequiredType(TRIGGER_DELEGATE_TYPE, delegateType)
        support.setSymbols(
            GenerateTriggerDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.TriggerDefinition", definitionType),
        )
        support.setSymbols(
            GenerateTriggerDelegate::class.qualifiedName!!,
            support.classDeclaration("com.example.TriggerDelegate", delegateType),
        )

        val processor = TriggerProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_TRIGGER_PROVIDER)
        assertTrue(generated.contains("object GeneratedTriggerProvider"))
        assertTrue(generated.contains("com.example.TriggerDefinition"))
        assertTrue(generated.contains("com.example.TriggerDelegate()"))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_TRIGGER_PROVIDER, any())
        }
    }
}
