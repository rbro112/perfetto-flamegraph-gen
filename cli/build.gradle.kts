import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "com.rbro.flamegraphgen"
version = "1.0.0"

application {
    mainClass.set("com.rbro.flamegraph.gen.cli.FlamegraphGenKt")
    mainClassName = mainClass.get()
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    implementation(libs.clikt)
    implementation(project(":flamegraph-gen"))
    testImplementation(kotlin("test"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "com.rbro.flamegraph.gen.cli.FlamegraphGenKt"
        }
        isZip64 = true
        archiveBaseName.set("perfetto-flamegraph-gen")
        // Ignores the "-all" classifier shadow uses by default
        archiveClassifier.set("")
    }
    register("executable") {
        logger.info("Packaging ${project.name} into an executable binary...")
        dependsOn("shadowJar")

        doLast {
            val jarFile = File(
                project.buildDir,
                "libs/perfetto-flamegraph-gen-${project.version}.jar"
            )
            require(jarFile.exists()) { "shadowJar output file at ${jarFile.canonicalPath} does not exist!" }
            val executableFile = File(project.buildDir, "libs/perfetto-flamegraph-gen")

            executableFile.apply {
                writeText("#!/usr/bin/env bash\nexec java -jar \$0 \"\$@\"\n")
                appendBytes(jarFile.readBytes())
                setExecutable(true)
            }
        }
    }
}

// TODO: As part of build, download and store prebuilt in local dir or in jar depending on config
data class TraceProcessorPrebuilt(
    val tool: String,
    val arch: String,
    val fileName: String,
    val fileSize: Long,
    val url: String,
    val sha256: String,
    val platform: String? = null,
    val machine: List<String> = emptyList()
) {
    fun outputFile(outputDir: String): File {
        return File(outputDir, "/$arch/$fileName")
    }
}

fun downloadPrebuilt(
    traceProcessorPrebuilt: TraceProcessorPrebuilt,
    destFile: File
) {
    ant.invokeMethod("get", mapOf("src" to traceProcessorPrebuilt.url, "dest" to destFile))
}

/**
 * From https://github.com/google/perfetto/blob/master/tools/trace_processor
 */
val traceProcessorPrebuiltMacAmd64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "mac-amd64",
    fileName = "trace_processor_shell",
    fileSize = 7532800,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/mac-amd64/trace_processor_shell",
    sha256 = "c2d81c2e3fac2fd2ee181f9aaaff0c0f33e21bf855c458912288ec0e6ffd80e3",
    platform = "darwin",
    machine = listOf("x86_64"),
)

val traceProcessorPrebuiltMacArm64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "mac-arm64",
    fileName = "trace_processor_shell",
    fileSize = 6526392,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/mac-arm64/trace_processor_shell",
    sha256 = "cfedeadf858da4daab61c2d997017802c13d06990127b3f717e5e241a16b4934",
    platform = "darwin",
    machine = listOf("arm64"),
)

val traceProcessorPrebuiltLinuxAmd64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "linux-amd64",
    fileName = "trace_processor_shell",
    fileSize = 8088384,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/linux-amd64/trace_processor_shell",
    sha256 = "74b437b8e71c3b5075e99087152f9100372041a13668baaeb02e889935633099",
    platform = "linux",
    machine = listOf("x86_64"),
)

val traceProcessorPrebuiltLinuxArm = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "linux-arm",
    fileName = "trace_processor_shell",
    fileSize = 5180284,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/linux-arm/trace_processor_shell",
    sha256 = "bca0829373c07fdcc03652e626dae31bdf3508d65febbbfb9b5fbf7b30807342",
    platform = "linux",
    machine = listOf("armv6l", "armv7l", "armv8l"),
)

val traceProcessorPrebuiltLinuxArm64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "linux-arm64",
    fileName = "trace_processor_shell",
    fileSize = 7215960,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/linux-arm64/trace_processor_shell",
    sha256 = "026d92e77a7af0fbaa39591002d93abce89d16ea5bff9da6814c49601ca702b4",
    platform = "linux",
    machine = listOf("aarch64"),
)

val traceProcessorPrebuiltAndroidArm = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "android-arm",
    fileName = "trace_processor_shell",
    fileSize = 4857904,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/android-arm/trace_processor_shell",
    sha256 = "15520a62d65f0b20b41308c4e89d2ee1e24bdc262d5964a55a999a07ef081d8f",
)

val traceProcessorPrebuiltAndroidArm64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "android-arm64",
    fileName = "trace_processor_shell",
    fileSize = 6502032,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/android-arm64/trace_processor_shell",
    sha256 = "89bdf50b25a2f3718415b6da48a0486b808c6e34a549f7c1771a59fc5325fd47",
)

val traceProcessorPrebuiltAndroidx86 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "android-x86",
    fileName = "trace_processor_shell",
    fileSize = 7155716,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/android-x86/trace_processor_shell",
    sha256 = "1cf08e8e07dc4f12376c1e58f831b12f36ee8210dfb9ccda4e0bc4fedff13e2f",
)

val traceProcessorPrebuiltAndroidx64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "android-x64",
    fileName = "trace_processor_shell",
    fileSize = 7681712,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/android-x64/trace_processor_shell",
    sha256 = "c466bd1fade9c433c24683dd3dc57acaf3a356e1268ae7b2085e7f5ceba5285c",
)

val traceProcessorPrebuiltWindowsAmd64 = TraceProcessorPrebuilt(
    tool = "trace_processor_shell",
    arch = "windows-amd64",
    fileName = "trace_processor_shell.exe",
    fileSize = 7080448,
    url = "https://commondatastorage.googleapis.com/perfetto-luci-artifacts/v26.1/windows-amd64/trace_processor_shell.exe",
    sha256 = "18649f0d6980839a2303ff4e7726114e96e700323018dbf74a579691df233bb5",
    platform = "win32",
    machine = listOf("amd64"),
)

val traceProcessorPrebuilts = listOf(
    traceProcessorPrebuiltMacAmd64,
    traceProcessorPrebuiltMacArm64,
    traceProcessorPrebuiltLinuxAmd64,
    traceProcessorPrebuiltLinuxArm,
    traceProcessorPrebuiltLinuxArm64,
    traceProcessorPrebuiltAndroidArm,
    traceProcessorPrebuiltAndroidArm64,
    traceProcessorPrebuiltAndroidx86,
    traceProcessorPrebuiltAndroidx64,
    traceProcessorPrebuiltWindowsAmd64,
)
