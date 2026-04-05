package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class ConstraintProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesConstraintProviderFile() {
        val definitionType = support.requiredType()
        val delegateType = support.requiredType()
        support.stubRequiredType(CONSTRAINT_DEFINITION_TYPE, definitionType)
        support.stubRequiredType(CONSTRAINT_DELEGATE_TYPE, delegateType)
        support.setSymbols(
            GenerateConstraintDefinition::class.qualifiedName!!,
            support.objectDeclaration("com.example.ConstraintDefinition", definitionType),
        )
        support.setSymbols(
            GenerateConstraintDelegate::class.qualifiedName!!,
            support.classDeclaration("com.example.ConstraintDelegate", delegateType),
        )

        val processor = ConstraintProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_CONSTRAINT_PROVIDER)
        assertTrue(generated.contains("object GeneratedConstraintProvider"))
        assertTrue(generated.contains("com.example.ConstraintDefinition"))
        assertTrue(generated.contains("com.example.ConstraintDelegate()"))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_CONSTRAINT_PROVIDER, any())
        }
    }
}
