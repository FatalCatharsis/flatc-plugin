package github.fatalcatharsis

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.gradle.testkit.runner.TaskOutcome

class FlatcCompileTest {
    @get:Rule
    val projectDir = TemporaryFolder()

    @Test
    fun `Functional test of header file generation in c++`() {
        // given
        writeFile(Paths.get("build.gradle.kts"), """
            import github.fatalcatharsis.FlatcCompile
            import github.fatalcatharsis.FlatcConvert

            plugins {
                `cpp-application`
                id("github.fatalcatharsis.flatc") version("1.0-SNAPSHOT")
            }

            tasks.create<FlatcCompile>("vec2") {
                input = file("./vec2.fbs")
            }
        """.trimIndent())

        writeFile(Paths.get("settings.gradle.kts"), """
            rootProject.name = "flatc"

            pluginManagement {
                repositories {
                    mavenLocal()
                    maven("https://plugins.gradle.org/m2/")
                }
            }
        """.trimIndent())

        writeFile(Paths.get("vec2.fbs"), """
            table Vec2 {
                x: float;
                y: float;
            }

            root_type Vec2;
        """.trimIndent())

        // when
        val result = GradleRunner.create()
            .withProjectDir(projectDir.root)
            .withPluginClasspath()
            .withArguments("build")
            .build()

        // then
        assertEquals(TaskOutcome.SUCCESS, result.task(":build")!!.outcome)
        assertTrue("Expected a header file to be generated by flatc", Files.exists(projectDir.root.toPath().resolve("./build/generated/headers/vec2_generated.h")))
    }

    private fun writeFile(filePath: Path, content: String) {
        if (filePath.parent != null) {
            val parentPath = projectDir.root.toPath().resolve(filePath.parent).normalize()
            Files.createDirectories(parentPath)
        } else {
            projectDir.root.toPath()
        }

        val outputStream = FileOutputStream(projectDir.root.resolve(filePath.toFile()))
        PrintWriter(outputStream).use {
            it.write(content)
        }
    }
}
