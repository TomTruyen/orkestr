package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class ReceiverProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesReceiverProviderFile() {
        val receiverType = support.requiredType()
        support.stubRequiredType(RECEIVER_FACTORY_TYPE, receiverType)
        support.setSymbols(
            GenerateReceiverFactory::class.qualifiedName!!,
            support.objectDeclaration("com.example.ReceiverFactory", receiverType),
        )

        val processor = ReceiverProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_RECEIVER_PROVIDER)
        assertTrue(generated.contains("object GeneratedReceiverProvider"))
        assertTrue(generated.contains("com.example.ReceiverFactory"))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_RECEIVER_PROVIDER, any())
        }
    }
}
