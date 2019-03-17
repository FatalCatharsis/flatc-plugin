package github.fatalcatharsis

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException
import org.gradle.kotlin.dsl.findPlugin
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.language.cpp.plugins.CppApplicationPlugin
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

open class FlatcConvert : FlatcTask(
    defaultOutputDir = Paths.get("./generated/json"),
    inputExtension = "json"
) {
    lateinit var template: File

    init {
        if (project.plugins.hasPlugin(CppApplicationPlugin::class)) {
            output = Paths.get("./install").toFile()
        }
    }

    @TaskAction
    fun action() {
        val plugin = project.plugins.findPlugin(FlatcPlugin::class)!!
        val files = getInputFiles()
        val flatcCall = plugin.localFlatcPath?.resolve("flatc.exe")?.normalize()?.toString()
            ?: Paths.get("flatc.exe")

        project.exec {
            commandLine(flatcCall, "-o", "$output", "-b", "$template", files.joinToString(" ") { "\"$it\"" })
        }
    }

    @InputFile
    fun getTemplateFile(): File {
        val templatePath = template.toPath()
        if (!Files.exists(templatePath) || !Files.isRegularFile(templatePath)) {
            throw TaskConfigurationException(
                path,
                "The  task named \"$name\" requires that the ${FlatcConvert::template.name} property reference an existing file.",
                null
            )
        }

        return template
    }
}
