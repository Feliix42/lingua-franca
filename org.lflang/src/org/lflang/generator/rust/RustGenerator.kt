package org.lflang.generator.rust

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import org.lflang.FileConfig
import org.lflang.Target
import org.lflang.generator.GeneratorBase
import org.lflang.lf.Action
import org.lflang.lf.VarRef


class VelocityGenerator(
    val templatePath: String,
) {

}

data class VCrateSpec(
    val name: String,
    val version: String,
    val author: String,
)

data class VRuntimeSpec(val toml_spec: String)


/**
 * Generates Rust code
 */
class RustGenerator : GeneratorBase() {


    override fun doGenerate(resource: Resource, fsa: IFileSystemAccess2, context: IGeneratorContext) {
        super.doGenerate(resource, fsa, context)

        // stop if there are any errors found in the program by doGenerate() in GeneratorBase
        if (generatorErrorsOccurred) return

        // abort if there is no main reactor
        if (mainDef == null) {
            println("WARNING: The given Lingua Franca program does not define a main reactor. Therefore, no code was generated.")
            return
        }

        generateFiles(fsa)

        if (targetConfig.noCompile || generatorErrorsOccurred) {
            println("Exiting before invoking target compiler.")
        } else {
            invokeRustCompiler()
        }
    }


    private fun invokeRustCompiler() {
        // let's assume we use cargo
        val outPath = fileConfig.outPath

        val buildPath = outPath.resolve("build").resolve(topLevelName)

        // make sure the build directory exists
        FileConfig.createDirectories(buildPath)

        val cargoBuilder = createCommand(
            "cargo", listOf("build"),
            outPath,
            "The Rust target requires Cargo in the path. " +
                    "Auto-compiling can be disabled using the \"no-compile: true\" target property.",
            true
        ) ?: return


        val cargoReturnCode = executeCommand(cargoBuilder)

        if (cargoReturnCode == 0) {
            println("SUCCESS (compiling generated Rust code)")
            println("Generated source code is in ${fileConfig.srcGenPath}")
            println("Compiled binary is in ${fileConfig.binPath}")
        } else {
            reportError("cargo failed with error code $cargoReturnCode")
        }
    }





    override fun supportsGenerics(): Boolean = true

    override fun getTargetTimeType(): String = "LogicalTime"

    override fun getTargetTagType(): String = "Tag"

    override fun getTargetTagIntervalType(): String = "Duration"

    override fun getTargetUndefinedType(): String = TODO("what's that")

    override fun getTargetFixedSizeListType(baseType: String, size: Int): String =
        "[ $baseType ; $size ]"

    override fun getTargetVariableSizeListType(baseType: String): String =
        "Vec<$baseType>"

    override fun getTarget(): Target = Target.Rust


    override fun generateDelayBody(action: Action, port: VarRef): String {
        TODO("Not yet implemented")
    }

    override fun generateForwardBody(action: Action, port: VarRef): String {
        TODO("Not yet implemented")
    }

    override fun generateDelayGeneric(): String {
        TODO("Not yet implemented")
    }

}
