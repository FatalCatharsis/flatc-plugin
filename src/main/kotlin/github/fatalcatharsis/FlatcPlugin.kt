package github.fatalcatharsis

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import org.gradle.language.cpp.CppApplication
import org.gradle.language.cpp.plugins.CppApplicationPlugin
import java.nio.file.Path

class FlatcPlugin : Plugin<Project> {
    internal var localFlatcPath: Path? = null
        private set

    private val flatcFetchHelper = FlatcFetchHelper()

    override fun apply(project: Project) {
        project.afterEvaluate {
            val buildTask = if (project.plugins.hasPlugin(CppApplicationPlugin::class)) {
                project.tasks.findByName("build")
            } else {
                null
            }

            project.tasks.withType(FlatcCompile::class).forEach {
                applySourceSets(project, it)
                buildTask?.dependsOn(it)
            }

            project.tasks.withType(FlatcConvert::class).forEach {
                buildTask?.dependsOn(it)
            }

            if (checkForFlatc(project)) {
                localFlatcPath = project.buildDir.toPath()
                flatcFetchHelper.fetch(localFlatcPath!!)
            }
        }
    }

    private fun applySourceSets(project: Project, task: FlatcTask) {
        if (project.plugins.hasPlugin(CppApplicationPlugin::class)) {
            val application = project.properties["application"] as CppApplication
            if (!application.privateHeaders.contains(task.output)) {
                application.privateHeaders { from(task.output) }
            }
        }
    }

    private fun checkForFlatc(project: Project): Boolean {
        val result = try {
            project.exec {
                commandLine("flatc", "--help")
                setIgnoreExitValue(false)
            }.exitValue
        } catch (e: Exception) {
            project.logger.log(LogLevel.INFO, "Flatc not found global, fetching it into local.")
            -1
        }
        return result != 0
    }
}
