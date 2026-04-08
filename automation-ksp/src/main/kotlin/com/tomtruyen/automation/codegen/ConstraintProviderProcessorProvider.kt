package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated

class ConstraintProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = ConstraintProviderProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )
}

private class ConstraintProviderProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val constraintDefinitions = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateConstraintDefinition::class.qualifiedName!!,
            requiredSuperType = CONSTRAINT_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT),
        ) ?: return emptyList()
        val constraintDelegates = collectAnnotatedClasses(
            resolver = resolver,
            logger = logger,
            annotationName = GenerateConstraintDelegate::class.qualifiedName!!,
            requiredSuperType = CONSTRAINT_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT),
        ) ?: return emptyList()

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_CONSTRAINT_PROVIDER,
            sourceFiles = combinedSourceFiles(constraintDefinitions, constraintDelegates),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import com.tomtruyen.automation.features.constraints.config.ConstraintConfig")
            appendLine("import com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition")
            appendLine("import com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate")
            appendLine("import android.content.Context")
            appendLine()
            appendGeneratedProviderObject(
                name = GENERATED_CONSTRAINT_PROVIDER,
                definitionType = "ConstraintDefinition<*>",
                definitions = constraintDefinitions.map { it.objectReference() },
                delegates = constraintDelegates.map { it.constraintInstantiationExpression() },
                delegateFunction = "fun delegates(context: Context): List<ConstraintDelegate<out ConstraintConfig>>",
            )
        }

        generated = true
        return emptyList()
    }
}
