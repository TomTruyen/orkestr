package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated

class ReceiverProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = ReceiverProviderProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )
}

private class ReceiverProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val receiverFactories = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateReceiverFactory::class.qualifiedName!!,
            requiredSuperType = RECEIVER_FACTORY_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_RECEIVER_PROVIDER,
            sourceFiles = combinedSourceFiles(receiverFactories),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver")
            appendLine()
            appendLine("object $GENERATED_RECEIVER_PROVIDER {")
            appendLine("    val factories: List<TriggerReceiver.TriggerFactory> = listOf(")
            appendGeneratedList(receiverFactories.map { it.objectReference() })
            appendLine("    )")
            appendLine("}")
        }

        generated = true
        return emptyList()
    }
}
