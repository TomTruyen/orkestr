package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class ActionProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesActionProviderFile() {
        val definitionType = support.requiredType()
        val delegateType = support.requiredType()
        val contextType = support.requiredType()
        support.stubRequiredType(ACTION_DEFINITION_TYPE, definitionType)
        support.stubRequiredType(ACTION_DELEGATE_TYPE, delegateType)
        support.setSymbols(
            GenerateActionDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.ActionDefinition", definitionType),
        )
        support.setSymbols(
            GenerateActionDelegate::class.qualifiedName!!,
            support.classDeclaration(
                qualifiedName = "com.example.ActionDelegate",
                implementedType = delegateType,
                constructorParameterTypes = listOf(contextType),
            ),
        )

        val processor = ActionProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_ACTION_PROVIDER)
        assertTrue(generated.contains("object GeneratedActionProvider"))
        assertTrue(generated.contains("com.example.ActionDefinition"))
        assertTrue(generated.contains("com.example.ActionDelegate(context)"))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_ACTION_PROVIDER, any())
        }
    }
}
