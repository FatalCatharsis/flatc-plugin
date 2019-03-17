package github.fatalcatharsis

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import org.gradle.execution.commandline.TaskConfigurationException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

abstract class FlatcTask(
    defaultOutputDir: Path,
    private val inputExtension: String
) : DefaultTask() {
    @Internal
    lateinit var input: File

    @OutputDirectory
    @Optional
    var output: File = project.buildDir.toPath().resolve(defaultOutputDir).toFile()
        set(value) {
            field = project.buildDir.toPath().resolve(value.toPath()).toFile()
        }

    @Input
    @Optional
    var recursive = false

    @InputFiles
    fun getInputFiles(): Collection<File> {
        val inputPath = input.toPath()
        if (!Files.exists(input.toPath()) || (!Files.isDirectory(inputPath) && !Files.isRegularFile(inputPath))) {
            throw TaskConfigurationException(
                path,
                "The task named \"$name\" requires that the property ${FlatcTask::input.name} reference a file or folder that exists.",
                null
            )
        }

        return if (Files.isDirectory(inputPath)) {
            val collection =
                project.fileTree(inputPath)
                    .matching { include("${if (recursive) "**/" else ""}*.$inputExtension") }
                    .files
            if (collection.isEmpty()) {
                throw TaskConfigurationException(
                    path,
                    "The input directory in task \"$name\" contained no *.$inputExtension files",
                    null
                )
            }
            collection
        } else {
            Collections.singleton(input)
        }
    }
}
