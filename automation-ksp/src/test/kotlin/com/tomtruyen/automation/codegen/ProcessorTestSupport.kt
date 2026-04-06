package com.tomtruyen.automation.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
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
import java.io.ByteArrayOutputStream

internal class ProcessorTestSupport {
    @MockK
    lateinit var environment: SymbolProcessorEnvironment

    @MockK
    lateinit var codeGenerator: CodeGenerator

    @MockK(relaxed = true)
    lateinit var logger: KSPLogger

    @MockK
    lateinit var resolver: Resolver

    lateinit var outputs: MutableMap<String, ByteArrayOutputStream>

    fun setUp() {
        MockKAnnotations.init(this)
        outputs = mutableMapOf()

        every { environment.codeGenerator } returns codeGenerator
        every { environment.logger } returns logger
        every { environment.options } returns emptyMap()
        every {
            codeGenerator.createNewFile(
                any(),
                "com.tomtruyen.automation.generated",
                any(),
                any(),
            )
        } answers {
            val fileName = thirdArg<String>()
            outputs.getOrPut(fileName) { ByteArrayOutputStream() }
        }
        every { resolver.getKSNameFromString(any()) } answers { ksName(firstArg()) }
    }

    fun setOptions(options: Map<String, String>) {
        every { environment.options } returns options
    }

    fun stubRequiredType(qualifiedName: String, type: KSType = requiredType()) {
        every {
            resolver.getClassDeclarationByName(
                match { it.asString() == qualifiedName },
            )
        } returns requiredDeclaration(type)
    }

    fun setSymbols(annotationName: String, vararg symbols: KSClassDeclaration) {
        every { resolver.getSymbolsWithAnnotation(annotationName) } returns symbols.asSequence()
    }

    fun output(fileName: String): String = outputs.getValue(fileName).toString()

    fun requiredType(): KSType = io.mockk.mockk {
        every { isAssignableFrom(any()) } returns true
    }

    fun objectDeclaration(qualifiedName: String, implementedType: KSType): KSClassDeclaration = declaration(
        qualifiedName = qualifiedName,
        classKind = ClassKind.OBJECT,
        implementedType = implementedType,
    )

    fun classDeclaration(
        qualifiedName: String,
        implementedType: KSType,
        constructorParameterTypes: List<KSType> = emptyList(),
    ): KSClassDeclaration {
        val contextDeclaration = declaration(
            qualifiedName = "android.content.Context",
            classKind = ClassKind.CLASS,
            implementedType = constructorParameterTypes.firstOrNull() ?: requiredType(),
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
            primaryConstructor = constructor,
        )
    }

    private fun requiredDeclaration(type: KSType): KSClassDeclaration = io.mockk.mockk {
        every { asStarProjectedType() } returns type
    }

    private fun declaration(
        qualifiedName: String,
        classKind: ClassKind,
        implementedType: KSType,
        primaryConstructor: KSFunctionDeclaration? = null,
    ): KSClassDeclaration {
        val file = io.mockk.mockk<KSFile>()
        val declaration = io.mockk.mockk<KSClassDeclaration> {
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
        every { file.declarations } returns sequenceOf(declaration)
        return declaration
    }

    private fun ksName(value: String): KSName = io.mockk.mockk {
        every { asString() } returns value
        every { getQualifier() } returns value.substringBeforeLast('.', "")
        every { getShortName() } returns value.substringAfterLast('.')
    }
}
