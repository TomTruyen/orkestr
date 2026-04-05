package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter
import java.io.File

internal fun CodeGenerator.writeGeneratedFile(
    fileName: String,
    sourceFiles: Array<KSFile>,
    content: StringBuilder.() -> Unit,
) {
    val file = createNewFile(
        dependencies = Dependencies(aggregating = true, *sourceFiles),
        packageName = GENERATED_PACKAGE,
        fileName = fileName,
    )

    OutputStreamWriter(file, Charsets.UTF_8).use { writer ->
        writer.write(buildString(content))
    }
}

internal fun StringBuilder.appendGeneratedProviderObject(
    name: String,
    definitionType: String,
    definitions: List<String>,
    delegates: List<String>,
    delegateFunction: String,
) {
    appendLine("object $name {")
    appendLine("    val definitions: List<$definitionType> = listOf(")
    appendGeneratedList(definitions)
    appendLine("    )")
    appendLine()
    appendLine("    $delegateFunction = listOf(")
    appendGeneratedList(delegates)
    appendLine("    )")
    appendLine("}")
}

internal fun StringBuilder.appendGeneratedList(entries: List<String>) {
    if (entries.isEmpty()) {
        appendLine("    ")
        return
    }

    entries.forEachIndexed { index, entry ->
        val suffix = if (index == entries.lastIndex) "" else ","
        appendLine("        $entry$suffix")
    }
}

internal fun KSClassDeclaration.objectReference(): String = qualifiedName?.asString()
    ?: error("Missing qualified name for ${simpleName.asString()}")

internal fun KSClassDeclaration.instantiationExpression(): String {
    val qualifiedName = objectReference()
    return when (classKind) {
        ClassKind.OBJECT -> qualifiedName
        ClassKind.CLASS -> {
            requireNoArgConstructor()
            "$qualifiedName()"
        }

        else -> error("Unsupported kind $classKind")
    }
}

internal fun KSClassDeclaration.actionInstantiationExpression(): String {
    val qualifiedName = objectReference()
    return when (classKind) {
        ClassKind.OBJECT -> qualifiedName
        ClassKind.CLASS -> {
            val constructor = primaryConstructor
                ?: constructors().singleOrNull()
                ?: error("Action delegate $qualifiedName must declare a single constructor.")
            val parameterTypes = constructor.parameters.map {
                it.type.resolve().declaration.qualifiedName?.asString().orEmpty()
            }
            when (parameterTypes) {
                emptyList<String>() -> "$qualifiedName()"
                listOf(CONTEXT_TYPE) -> "$qualifiedName(context)"
                else -> error(
                    "Action delegate $qualifiedName must have either a no-arg constructor or a single Context constructor.",
                )
            }
        }

        else -> error("Unsupported kind $classKind")
    }
}

private fun KSClassDeclaration.requireNoArgConstructor() {
    val constructor = primaryConstructor
        ?: constructors().singleOrNull()
        ?: error("Delegate ${qualifiedName?.asString()} must declare a no-arg constructor.")
    if (constructor.parameters.isNotEmpty()) {
        error("Delegate ${qualifiedName?.asString()} must declare a no-arg constructor.")
    }
}

private fun KSClassDeclaration.constructors(): List<KSFunctionDeclaration> =
    declarations.filterIsInstance<KSFunctionDeclaration>().filter {
        it.simpleName.asString() == "<init>"
    }.toList()

internal data class SqlMigrationSpec(
    val startVersion: Int,
    val endVersion: Int,
    val assetPath: String,
)

internal fun collectAnnotatedClasses(
    resolver: Resolver,
    logger: KSPLogger,
    annotationName: String,
    requiredSuperType: String,
    allowedKinds: Set<ClassKind>,
): List<KSClassDeclaration>? {
    val symbols = resolver.getSymbolsWithAnnotation(annotationName).toList()
    val deferred = symbols.filterNot { it.validate() }
    if (deferred.isNotEmpty()) return null

    val requiredType = resolver.getClassDeclarationByName(
        resolver.getKSNameFromString(requiredSuperType),
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
            logger.error("@$annotationName cannot target ${declaration.classKind}.", declaration)
            return@mapNotNull null
        }

        if (!requiredType.isAssignableFrom(declaration.asStarProjectedType())) {
            logger.error(
                "${declaration.qualifiedName?.asString()} must implement or extend $requiredSuperType.",
                declaration,
            )
            return@mapNotNull null
        }

        declaration
    }.sortedBy { it.qualifiedName?.asString().orEmpty() }
}

internal fun loadMigrationSpecs(
    options: Map<String, String>,
    logger: KSPLogger,
): List<SqlMigrationSpec> {
    val migrationDir = options[MIGRATION_DIR_OPTION]?.takeIf(String::isNotBlank) ?: return emptyList()
    val directory = File(migrationDir)
    if (!directory.exists()) {
        logger.warn("Migration directory does not exist: $migrationDir")
        return emptyList()
    }

    return directory.listFiles()
        .orEmpty()
        .filter { it.isFile && it.extension == "sql" }
        .mapNotNull { file ->
            MIGRATION_FILENAME_REGEX.matchEntire(file.name)?.destructured?.let { (start, end) ->
                SqlMigrationSpec(
                    startVersion = start.toInt(),
                    endVersion = end.toInt(),
                    assetPath = "$MIGRATION_ASSET_PREFIX/${file.name}",
                )
            } ?: run {
                logger.warn("Ignoring migration file with unsupported name: ${file.name}")
                null
            }
        }
        .sortedWith(compareBy(SqlMigrationSpec::startVersion, SqlMigrationSpec::endVersion))
}

internal fun combinedSourceFiles(vararg groups: List<KSClassDeclaration>): Array<KSFile> =
    groups.asSequence()
        .flatMap(List<KSClassDeclaration>::asSequence)
        .mapNotNull(KSClassDeclaration::containingFile)
        .distinct()
        .toList()
        .toTypedArray()

internal fun migrationExpression(spec: SqlMigrationSpec): String =
    "SqlAssetMigration(context, ${spec.startVersion}, ${spec.endVersion}, \"${spec.assetPath}\")"

internal const val CONTEXT_TYPE = "android.content.Context"
internal const val GENERATED_PACKAGE = "com.tomtruyen.automation.generated"
internal const val GENERATED_TRIGGER_PROVIDER = "GeneratedTriggerProvider"
internal const val GENERATED_CONSTRAINT_PROVIDER = "GeneratedConstraintProvider"
internal const val GENERATED_ACTION_PROVIDER = "GeneratedActionProvider"
internal const val GENERATED_RECEIVER_PROVIDER = "GeneratedReceiverProvider"
internal const val GENERATED_MIGRATION_PROVIDER = "GeneratedMigrationProvider"
internal const val GENERATED_REGISTRY_PROVIDER = "GeneratedAutomationRegistryProvider"
internal const val MIGRATION_DIR_OPTION = "automation.migrations.dir"
internal const val MIGRATION_ASSET_PREFIX = "migrations"
internal const val TRIGGER_DEFINITION_TYPE =
    "com.tomtruyen.automation.features.triggers.definition.TriggerDefinition"
internal const val TRIGGER_DELEGATE_TYPE =
    "com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate"
internal const val CONSTRAINT_DEFINITION_TYPE =
    "com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition"
internal const val CONSTRAINT_DELEGATE_TYPE =
    "com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate"
internal const val ACTION_DEFINITION_TYPE =
    "com.tomtruyen.automation.features.actions.definition.ActionDefinition"
internal const val ACTION_DELEGATE_TYPE =
    "com.tomtruyen.automation.features.actions.delegate.ActionDelegate"
internal const val RECEIVER_FACTORY_TYPE =
    "com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver.TriggerFactory"
internal val MIGRATION_FILENAME_REGEX = Regex("""(\d+)_(\d+)\.sql""")
