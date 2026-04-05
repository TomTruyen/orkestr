package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated

class TriggerProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = TriggerProviderProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )
}

private class TriggerProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val triggerDefinitions = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateTriggerDefinition::class.qualifiedName!!,
            requiredSuperType = TRIGGER_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()
        val triggerDelegates = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateTriggerDelegate::class.qualifiedName!!,
            requiredSuperType = TRIGGER_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT),
        ) ?: return emptyList()

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_TRIGGER_PROVIDER,
            sourceFiles = combinedSourceFiles(triggerDefinitions, triggerDelegates),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import com.tomtruyen.automation.features.triggers.config.TriggerConfig")
            appendLine("import com.tomtruyen.automation.features.triggers.definition.TriggerDefinition")
            appendLine("import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate")
            appendLine()
            appendGeneratedProviderObject(
                name = GENERATED_TRIGGER_PROVIDER,
                definitionType = "TriggerDefinition<*>",
                definitions = triggerDefinitions.map { it.objectReference() },
                delegates = triggerDelegates.map { it.instantiationExpression() },
                delegateFunction = "fun delegates(): List<TriggerDelegate<out TriggerConfig>>",
            )
        }

        generated = true
        return emptyList()
    }
}
