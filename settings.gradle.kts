@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            alias("clikt").to("com.github.ajalt.clikt:clikt:3.5.0")
            alias("coroutines").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

            alias("kotlinx-serialization").to("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        }

        create("testlibs") {
            alias("junit-core").to("junit:junit:4.13.2")
            alias("junit-ext").to("androidx.test.ext:junit:1.1.3")
        }
    }
}

rootProject.name = "perfetto-flamegraph-generation"
include(":cli")
include(":flamegraph-gen")
