package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated

class AutomationRegistryProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        AutomationRegistryProviderProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
}

private class AutomationRegistryProviderProcessor(
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
        val constraintDefinitions = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateConstraintDefinition::class.qualifiedName!!,
            requiredSuperType = CONSTRAINT_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()
        val actionDefinitions = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateActionDefinition::class.qualifiedName!!,
            requiredSuperType = ACTION_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_REGISTRY_PROVIDER,
            sourceFiles = combinedSourceFiles(triggerDefinitions, constraintDefinitions, actionDefinitions),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry")
            appendLine()
            appendLine("object $GENERATED_REGISTRY_PROVIDER {")
            appendLine("    fun create(): AutomationDefinitionRegistry = AutomationDefinitionRegistry(")
            appendLine("        triggers = $GENERATED_TRIGGER_PROVIDER.definitions,")
            appendLine("        constraints = $GENERATED_CONSTRAINT_PROVIDER.definitions,")
            appendLine("        actions = $GENERATED_ACTION_PROVIDER.definitions")
            appendLine("    )")
            appendLine("}")
        }

        generated = true
        return emptyList()
    }
}
