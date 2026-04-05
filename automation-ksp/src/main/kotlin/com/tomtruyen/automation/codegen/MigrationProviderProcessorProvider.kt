package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class MigrationProviderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = MigrationProviderProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
        options = environment.options,
    )
}

private class MigrationProviderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val migrations = loadMigrationSpecs(options, logger)

        codeGenerator.writeGeneratedFile(
            fileName = GENERATED_MIGRATION_PROVIDER,
            sourceFiles = emptyArray(),
        ) {
            appendLine("package $GENERATED_PACKAGE")
            appendLine()
            appendLine("import android.content.Context")
            appendLine("import androidx.room.migration.Migration")
            appendLine("import com.tomtruyen.automation.data.migration.SqlAssetMigration")
            appendLine()
            appendLine("object $GENERATED_MIGRATION_PROVIDER {")
            appendLine("    fun migrations(context: Context): Array<Migration> = arrayOf(")
            appendGeneratedList(migrations.map(::migrationExpression))
            appendLine("    )")
            appendLine("}")
        }

        generated = true
        return emptyList()
    }
}
