buildscript {

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.21"))
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}
