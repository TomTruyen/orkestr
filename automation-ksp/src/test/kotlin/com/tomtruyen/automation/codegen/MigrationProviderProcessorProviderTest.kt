package com.tomtruyen.automation.codegen

import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.io.path.createTempDirectory

internal class MigrationProviderProcessorProviderTest {
    private val support = ProcessorTestSupport()

    @Before
    fun setUp() {
        support.setUp()
    }

    @Test
    fun process_generatesMigrationProviderFile() {
        val migrationsDir = createTempDirectory(prefix = "automation-ksp-migrations").toFile().apply {
            resolve("1_2.sql").writeText("CREATE TABLE geofences(id TEXT PRIMARY KEY);")
        }
        support.setOptions(mapOf(MIGRATION_DIR_OPTION to migrationsDir.absolutePath))

        val processor = MigrationProviderProcessorProvider().create(support.environment)
        processor.process(support.resolver)
        processor.process(support.resolver)

        val generated = support.output(GENERATED_MIGRATION_PROVIDER)
        assertTrue(generated.contains("object GeneratedMigrationProvider"))
        assertTrue(generated.contains("""SqlAssetMigration(context, 1, 2, "migrations/1_2.sql")"""))
        verify(exactly = 1) {
            support.codeGenerator.createNewFile(any(), GENERATED_PACKAGE, GENERATED_MIGRATION_PROVIDER, any())
        }
        migrationsDir.deleteRecursively()
    }
}
