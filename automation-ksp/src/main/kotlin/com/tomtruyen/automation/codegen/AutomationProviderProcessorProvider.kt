package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class AutomationProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutomationProviderProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}

private class AutomationProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val triggerDefinitions = collect(
            resolver = resolver,
            annotationName = GenerateTriggerDefinition::class.qualifiedName!!,
            requiredSuperType = TRIGGER_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT)
        ) ?: return emptyList()

        val triggerDelegates = collect(
            resolver = resolver,
            annotationName = GenerateTriggerDelegate::class.qualifiedName!!,
            requiredSuperType = TRIGGER_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT)
        ) ?: return emptyList()

        val constraintDefinitions = collect(
            resolver = resolver,
            annotationName = GenerateConstraintDefinition::class.qualifiedName!!,
            requiredSuperType = CONSTRAINT_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT)
        ) ?: return emptyList()

        val constraintDelegates = collect(
            resolver = resolver,
            annotationName = GenerateConstraintDelegate::class.qualifiedName!!,
            requiredSuperType = CONSTRAINT_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT)
        ) ?: return emptyList()

        val actionDefinitions = collect(
            resolver = resolver,
            annotationName = GenerateActionDefinition::class.qualifiedName!!,
            requiredSuperType = ACTION_DEFINITION_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT)
        ) ?: return emptyList()

        val actionDelegates = collect(
            resolver = resolver,
            annotationName = GenerateActionDelegate::class.qualifiedName!!,
            requiredSuperType = ACTION_DELEGATE_TYPE,
            allowedKinds = setOf(ClassKind.CLASS, ClassKind.OBJECT)
        ) ?: return emptyList()

        val receiverFactories = collect(
            resolver = resolver,
            annotationName = GenerateReceiverFactory::class.qualifiedName!!,
            requiredSuperType = RECEIVER_FACTORY_TYPE,
            allowedKinds = setOf(ClassKind.OBJECT)
        ) ?: return emptyList()

        generateProviders(
            sourceFiles = (
                triggerDefinitions +
                    triggerDelegates +
                    constraintDefinitions +
                    constraintDelegates +
                    actionDefinitions +
                    actionDelegates +
                    receiverFactories
                ).mapNotNull { it.containingFile }.distinct().toTypedArray(),
            triggerDefinitions = triggerDefinitions,
            triggerDelegates = triggerDelegates,
            constraintDefinitions = constraintDefinitions,
            constraintDelegates = constraintDelegates,
            actionDefinitions = actionDefinitions,
            actionDelegates = actionDelegates,
            receiverFactories = receiverFactories
        )

        generated = true
        return emptyList()
    }

    private fun collect(
        resolver: Resolver,
        annotationName: String,
        requiredSuperType: String,
        allowedKinds: Set<ClassKind>
    ): List<KSClassDeclaration>? {
        val symbols = resolver.getSymbolsWithAnnotation(annotationName).toList()
        val deferred = symbols.filterNot { it.validate() }
        if (deferred.isNotEmpty()) return null

        val requiredType = resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(requiredSuperType)
        )?.asStarProjectedType() ?: run {
            logger.error("Unable to resolve required type $requiredSuperType")
            return emptyList()
        }

        return symbols.mapNotNull { symbol ->
            val declaration = symbol as? KSClassDeclaration ?: run {
                logger.error("@$annotationName can only target classes or objects.", symbol)
                return@mapNotNull null
            }

            if (declaration.classKind !in allowedKinds) {
                logger.error(
                    "@$annotationName cannot target ${declaration.classKind}.",
                    declaration
                )
                return@mapNotNull null
            }

            if (!requiredType.isAssignableFrom(declaration.asStarProjectedType())) {
                logger.error(
                    "${declaration.qualifiedName?.asString()} must implement or extend $requiredSuperType.",
                    declaration
                )
                return@mapNotNull null
            }

            declaration
        }.sortedBy { it.qualifiedName?.asString().orEmpty() }
    }

    private fun generateProviders(
        sourceFiles: Array<com.google.devtools.ksp.symbol.KSFile>,
        triggerDefinitions: List<KSClassDeclaration>,
        triggerDelegates: List<KSClassDeclaration>,
        constraintDefinitions: List<KSClassDeclaration>,
        constraintDelegates: List<KSClassDeclaration>,
        actionDefinitions: List<KSClassDeclaration>,
        actionDelegates: List<KSClassDeclaration>,
        receiverFactories: List<KSClassDeclaration>
    ) {
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, *sourceFiles),
            packageName = GENERATED_PACKAGE,
            fileName = GENERATED_FILE_NAME
        )

        OutputStreamWriter(file, Charsets.UTF_8).use { writer ->
            writer.write(
                buildString {
                    appendLine("package $GENERATED_PACKAGE")
                    appendLine()
                    appendLine("import android.content.Context")
                    appendLine("import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry")
                    appendLine("import com.tomtruyen.automation.features.actions.config.ActionConfig")
                    appendLine("import com.tomtruyen.automation.features.actions.definition.ActionDefinition")
                    appendLine("import com.tomtruyen.automation.features.actions.delegate.ActionDelegate")
                    appendLine("import com.tomtruyen.automation.features.constraints.config.ConstraintConfig")
                    appendLine("import com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition")
                    appendLine("import com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate")
                    appendLine("import com.tomtruyen.automation.features.triggers.config.TriggerConfig")
                    appendLine("import com.tomtruyen.automation.features.triggers.definition.TriggerDefinition")
                    appendLine("import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate")
                    appendLine("import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver")
                    appendLine()

                    appendProviderObject(
                        name = "GeneratedTriggerProvider",
                        definitionType = "TriggerDefinition<*>",
                        definitions = triggerDefinitions.map(::objectReference),
                        delegateType = "TriggerDelegate<out TriggerConfig>",
                        delegates = triggerDelegates.map(::instantiationExpression),
                        delegateFunction = "fun delegates(): List<TriggerDelegate<out TriggerConfig>>"
                    )
                    appendLine()
                    appendProviderObject(
                        name = "GeneratedConstraintProvider",
                        definitionType = "ConstraintDefinition<*>",
                        definitions = constraintDefinitions.map(::objectReference),
                        delegateType = "ConstraintDelegate<out ConstraintConfig>",
                        delegates = constraintDelegates.map(::instantiationExpression),
                        delegateFunction = "fun delegates(): List<ConstraintDelegate<out ConstraintConfig>>"
                    )
                    appendLine()
                    appendProviderObject(
                        name = "GeneratedActionProvider",
                        definitionType = "ActionDefinition<*>",
                        definitions = actionDefinitions.map(::objectReference),
                        delegateType = "ActionDelegate<out ActionConfig>",
                        delegates = actionDelegates.map(::actionInstantiationExpression),
                        delegateFunction = "fun delegates(context: Context): List<ActionDelegate<out ActionConfig>>"
                    )
                    appendLine()
                    appendLine("object GeneratedReceiverProvider {")
                    appendLine("    val factories: List<TriggerReceiver.TriggerFactory> = listOf(")
                    appendList(receiverFactories.map(::objectReference))
                    appendLine("    )")
                    appendLine("}")
                    appendLine()
                    appendLine("object GeneratedAutomationRegistryProvider {")
                    appendLine("    fun create(): AutomationDefinitionRegistry = AutomationDefinitionRegistry(")
                    appendLine("        triggers = GeneratedTriggerProvider.definitions,")
                    appendLine("        constraints = GeneratedConstraintProvider.definitions,")
                    appendLine("        actions = GeneratedActionProvider.definitions")
                    appendLine("    )")
                    appendLine("}")
                }
            )
        }
    }

    private fun StringBuilder.appendProviderObject(
        name: String,
        definitionType: String,
        definitions: List<String>,
        delegateType: String,
        delegates: List<String>,
        delegateFunction: String
    ) {
        appendLine("object $name {")
        appendLine("    val definitions: List<$definitionType> = listOf(")
        appendList(definitions)
        appendLine("    )")
        appendLine()
        appendLine("    $delegateFunction = listOf(")
        appendList(delegates)
        appendLine("    )")
        appendLine("}")
    }

    private fun StringBuilder.appendList(entries: List<String>) {
        if (entries.isEmpty()) {
            appendLine("    ")
            return
        }

        entries.forEachIndexed { index, entry ->
            val suffix = if (index == entries.lastIndex) "" else ","
            appendLine("        $entry$suffix")
        }
    }

    private fun objectReference(declaration: KSClassDeclaration): String =
        declaration.qualifiedName?.asString()
            ?: error("Missing qualified name for ${declaration.simpleName.asString()}")

    private fun instantiationExpression(declaration: KSClassDeclaration): String {
        val qualifiedName = objectReference(declaration)
        return when (declaration.classKind) {
            ClassKind.OBJECT -> qualifiedName
            ClassKind.CLASS -> {
                requireNoArgConstructor(declaration)
                "$qualifiedName()"
            }
            else -> error("Unsupported kind ${declaration.classKind}")
        }
    }

    private fun actionInstantiationExpression(declaration: KSClassDeclaration): String {
        val qualifiedName = objectReference(declaration)
        return when (declaration.classKind) {
            ClassKind.OBJECT -> qualifiedName
            ClassKind.CLASS -> {
                val constructor = declaration.primaryConstructor
                    ?: declaration.constructors().singleOrNull()
                    ?: error("Action delegate $qualifiedName must declare a single constructor.")
                val parameterTypes = constructor.parameters.map {
                    it.type.resolve().declaration.qualifiedName?.asString().orEmpty()
                }
                when (parameterTypes) {
                    emptyList<String>() -> "$qualifiedName()"
                    listOf(CONTEXT_TYPE) -> "$qualifiedName(context)"
                    else -> error(
                        "Action delegate $qualifiedName must have either a no-arg constructor or a single Context constructor."
                    )
                }
            }
            else -> error("Unsupported kind ${declaration.classKind}")
        }
    }

    private fun requireNoArgConstructor(declaration: KSClassDeclaration) {
        val constructor = declaration.primaryConstructor
            ?: declaration.constructors().singleOrNull()
            ?: error(
                "Delegate ${declaration.qualifiedName?.asString()} must declare a no-arg constructor."
            )
        if (constructor.parameters.isNotEmpty()) {
            error("Delegate ${declaration.qualifiedName?.asString()} must declare a no-arg constructor.")
        }
    }

    private companion object {
        private const val GENERATED_PACKAGE = "com.tomtruyen.automation.generated"
        private const val GENERATED_FILE_NAME = "GeneratedAutomationProviders"
        private const val CONTEXT_TYPE = "android.content.Context"
        private const val TRIGGER_DEFINITION_TYPE =
            "com.tomtruyen.automation.features.triggers.definition.TriggerDefinition"
        private const val TRIGGER_DELEGATE_TYPE =
            "com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate"
        private const val CONSTRAINT_DEFINITION_TYPE =
            "com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition"
        private const val CONSTRAINT_DELEGATE_TYPE =
            "com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate"
        private const val ACTION_DEFINITION_TYPE =
            "com.tomtruyen.automation.features.actions.definition.ActionDefinition"
        private const val ACTION_DELEGATE_TYPE =
            "com.tomtruyen.automation.features.actions.delegate.ActionDelegate"
        private const val RECEIVER_FACTORY_TYPE =
            "com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver.TriggerFactory"
    }
}

private fun KSClassDeclaration.constructors(): List<KSFunctionDeclaration> =
    declarations.filterIsInstance<KSFunctionDeclaration>().filter {
        it.simpleName.asString() == "<init>"
    }.toList()
