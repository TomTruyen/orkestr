package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

internal class AutomationProviderProcessorProviderTest {
    @MockK
    private lateinit var environment: SymbolProcessorEnvironment

    @MockK
    private lateinit var codeGenerator: CodeGenerator

    @MockK(relaxed = true)
    private lateinit var logger: KSPLogger

    @MockK
    private lateinit var resolver: Resolver

    private lateinit var output: ByteArrayOutputStream

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        output = ByteArrayOutputStream()

        every { environment.codeGenerator } returns codeGenerator
        every { environment.logger } returns logger
        every {
            codeGenerator.createNewFile(any(), "com.tomtruyen.automation.generated", "GeneratedAutomationProviders", any())
        } returns output
    }

    @Test
    fun create_returnsProcessor() {
        val processor = AutomationProviderProcessorProvider().create(environment)

        assertNotNull(processor)
        assertTrue(processor is SymbolProcessor)
    }

    @Test
    fun process_generatesProviderObjectsAndOnlyWritesOnce() {
        val triggerDefinitionType = requiredType()
        val triggerDelegateType = requiredType()
        val constraintDefinitionType = requiredType()
        val constraintDelegateType = requiredType()
        val actionDefinitionType = requiredType()
        val actionDelegateType = requiredType()
        val receiverFactoryType = requiredType()
        val contextType = requiredType()

        every { resolver.getKSNameFromString(any()) } answers { ksName(firstArg()) }
        every { resolver.getClassDeclarationByName(match { it.asString() == GenerateTriggerDefinition::class.qualifiedName }) } returns null

        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.triggers.definition.TriggerDefinition" }) } returns requiredDeclaration(triggerDefinitionType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate" }) } returns requiredDeclaration(triggerDelegateType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition" }) } returns requiredDeclaration(constraintDefinitionType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate" }) } returns requiredDeclaration(constraintDelegateType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.actions.definition.ActionDefinition" }) } returns requiredDeclaration(actionDefinitionType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.actions.delegate.ActionDelegate" }) } returns requiredDeclaration(actionDelegateType)
        every { resolver.getClassDeclarationByName(match { it.asString() == "com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver.TriggerFactory" }) } returns requiredDeclaration(receiverFactoryType)

        val triggerDefinition = objectDeclaration(
            "com.example.TriggerDefinition",
            triggerDefinitionType
        )
        val triggerDelegate = classDeclaration(
            qualifiedName = "com.example.TriggerDelegate",
            implementedType = triggerDelegateType
        )
        val constraintDefinition = objectDeclaration(
            "com.example.ConstraintDefinition",
            constraintDefinitionType
        )
        val constraintDelegate = classDeclaration(
            qualifiedName = "com.example.ConstraintDelegate",
            implementedType = constraintDelegateType
        )
        val actionDefinition = objectDeclaration(
            "com.example.ActionDefinition",
            actionDefinitionType
        )
        val actionDelegate = classDeclaration(
            qualifiedName = "com.example.ActionDelegate",
            implementedType = actionDelegateType,
            constructorParameterTypes = listOf(contextType)
        )
        val receiverFactory = objectDeclaration(
            "com.example.ReceiverFactory",
            receiverFactoryType
        )

        every { resolver.getSymbolsWithAnnotation(GenerateTriggerDefinition::class.qualifiedName!!) } returns sequenceOf(triggerDefinition)
        every { resolver.getSymbolsWithAnnotation(GenerateTriggerDelegate::class.qualifiedName!!) } returns sequenceOf(triggerDelegate)
        every { resolver.getSymbolsWithAnnotation(GenerateConstraintDefinition::class.qualifiedName!!) } returns sequenceOf(constraintDefinition)
        every { resolver.getSymbolsWithAnnotation(GenerateConstraintDelegate::class.qualifiedName!!) } returns sequenceOf(constraintDelegate)
        every { resolver.getSymbolsWithAnnotation(GenerateActionDefinition::class.qualifiedName!!) } returns sequenceOf(actionDefinition)
        every { resolver.getSymbolsWithAnnotation(GenerateActionDelegate::class.qualifiedName!!) } returns sequenceOf(actionDelegate)
        every { resolver.getSymbolsWithAnnotation(GenerateReceiverFactory::class.qualifiedName!!) } returns sequenceOf(receiverFactory)

        val processor = AutomationProviderProcessorProvider().create(environment)

        processor.process(resolver)
        processor.process(resolver)

        val generated = output.toString()
        assertTrue(generated.contains("object GeneratedTriggerProvider"))
        assertTrue(generated.contains("com.example.TriggerDefinition"))
        assertTrue(generated.contains("com.example.ActionDelegate(context)"))
        assertTrue(generated.contains("object GeneratedReceiverProvider"))
        verify(exactly = 1) {
            codeGenerator.createNewFile(any(), "com.tomtruyen.automation.generated", "GeneratedAutomationProviders", any())
        }
    }

    private fun requiredType(): KSType = io.mockk.mockk {
        every { isAssignableFrom(any()) } returns true
    }

    private fun requiredDeclaration(type: KSType): KSClassDeclaration = io.mockk.mockk {
        every { asStarProjectedType() } returns type
    }

    private fun objectDeclaration(
        qualifiedName: String,
        implementedType: KSType
    ): KSClassDeclaration = declaration(
        qualifiedName = qualifiedName,
        classKind = ClassKind.OBJECT,
        implementedType = implementedType
    )

    private fun classDeclaration(
        qualifiedName: String,
        implementedType: KSType,
        constructorParameterTypes: List<KSType> = emptyList()
    ): KSClassDeclaration {
        val contextDeclaration = declaration(
            qualifiedName = "android.content.Context",
            classKind = ClassKind.CLASS,
            implementedType = constructorParameterTypes.firstOrNull() ?: requiredType()
        )
        val constructor = io.mockk.mockk<KSFunctionDeclaration> {
            every { parameters } returns constructorParameterTypes.map {
                io.mockk.mockk<KSValueParameter> {
                    every { type } returns io.mockk.mockk<KSTypeReference> {
                        every { resolve() } returns io.mockk.mockk {
                            every { declaration } returns contextDeclaration
                        }
                    }
                }
            }
        }

        return declaration(
            qualifiedName = qualifiedName,
            classKind = ClassKind.CLASS,
            implementedType = implementedType,
            primaryConstructor = constructor
        )
    }

    private fun declaration(
        qualifiedName: String,
        classKind: ClassKind,
        implementedType: KSType,
        primaryConstructor: KSFunctionDeclaration? = null
    ): KSClassDeclaration {
        val file = io.mockk.mockk<KSFile>()
        return io.mockk.mockk {
            every { this@mockk.classKind } returns classKind
            every { this@mockk.qualifiedName } returns ksName(qualifiedName)
            every { this@mockk.simpleName } returns ksName(qualifiedName.substringAfterLast('.'))
            every { this@mockk.primaryConstructor } returns primaryConstructor
            every { this@mockk.declarations } returns emptySequence<KSDeclaration>()
            every { this@mockk.parent } returns file
            every { this@mockk.containingFile } returns file
            every { this@mockk.asStarProjectedType() } returns implementedType
            every { this@mockk.accept<KSNode?, Boolean>(any(), any()) } returns true
        }
    }

    private fun ksName(value: String): KSName = io.mockk.mockk {
        every { asString() } returns value
        every { getQualifier() } returns value.substringBeforeLast('.', "")
        every { getShortName() } returns value.substringAfterLast('.')
    }
}
