package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated

class ActionProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = ActionProviderProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )
}

private class ActionProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val actionDefinitions = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateActionDefinition::class.qualifiedName!!,
            requiredSuperType = ACTION_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()
        val actionDelegates = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateActionDelegate::class.qualifiedName!!,
            requiredSuperType = ACTION_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT),
        ) ?: return emptyList()

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_ACTION_PROVIDER,
            sourceFiles = combinedSourceFiles(actionDefinitions, actionDelegates),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import android.content.Context")
            appendLine("import com.tomtruyen.automation.features.actions.config.ActionConfig")
            appendLine("import com.tomtruyen.automation.features.actions.definition.ActionDefinition")
            appendLine("import com.tomtruyen.automation.features.actions.delegate.ActionDelegate")
            appendLine()
            appendGeneratedProviderObject(
                name = GENERATED_ACTION_PROVIDER,
                definitionType = "ActionDefinition<*>",
                definitions = actionDefinitions.map { it.objectReference() },
                delegates = actionDelegates.map { it.actionInstantiationExpression() },
                delegateFunction = "fun delegates(context: Context): List<ActionDelegate<out ActionConfig>>",
            )
        }

        generated = true
        return emptyList()
    }
}
