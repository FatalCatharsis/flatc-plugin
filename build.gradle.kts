import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.1"
}

group = "github.fatalcatharsis"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    jcenter()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    compile(kotlin("stdlib-jdk8"))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.25.0")
}

gradlePlugin {
    plugins {
        create("flatc") {
            id = "github.fatalcatharsis.flatc"
            implementationClass = "github.fatalcatharsis.FlatcPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/FatalCatharsis/flatc"
    vcsUrl = "https://github.com/FatalCatharsis/flatc"
    description = "A plugin that uses google flatbuffers compiler to generate source and binary files."

    (plugins) {

        // first plugin
        "flatc" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Flatc plugin"
            tags = listOf("individual", "tags", "per", "plugin")
            version = project.version as String
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "github.fatalcatharsis"
            artifactId = "flatc"
            version = project.version as String
        }
    }
}
