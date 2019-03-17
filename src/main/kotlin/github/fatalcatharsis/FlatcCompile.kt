package github.fatalcatharsis

import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findPlugin
import java.nio.file.Paths

open class FlatcCompile : FlatcTask(
    defaultOutputDir = Paths.get("./generated/headers"),
    inputExtension = "fbs"
) {
    @TaskAction
    fun action() {
        val plugin = project.plugins.findPlugin(FlatcPlugin::class)!!
        val files = getInputFiles()
        val flatcCall = plugin.localFlatcPath?.resolve("flatc.exe")?.normalize()?.toString()
            ?: "flatc"

        project.exec {
            commandLine(flatcCall, "--cpp", "--gen-object-api", "-o", "$output", files.joinToString(" ") { "\"$it\"" })
        }
    }
}
