package com.emergetools.perfetto.flamegraph.generation

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteExisting
import kotlin.io.path.pathString
import kotlin.io.path.writeText

class PerfettoTrace(val path: Path) {

    private val traceProcessorWorkingDir by lazy {
        createTempDirectory("perfetto_shell")
    }

    private val executable by lazy {
        createPerfettoTraceProcessor(traceProcessorWorkingDir)
    }

    /**
     * Results will be in csv format, with the first line being the header/columns
     * and the remaining lines being the data.
     */
    fun runQuery(
        query: String
    ): String {

        val queryFile = kotlin.io.path.createTempFile("trace_processor_query.sql")
        try {
            queryFile.writeText(query)

            val process = ProcessBuilder(
                executable.pathString,
                "--query-file",
                queryFile.pathString,
                path.pathString,
            ).start()
            process.waitFor()
            return process.inputStream.readBytes().toString(Charset.defaultCharset())
        } finally {
            queryFile.deleteExisting()
        }
    }

    companion object {

        // TODO: Better way to get file automatically for run and for jar
        fun createPerfettoTraceProcessor(workDir: Path): Path {
            val traceProcessorFile = System.getenv()["TRACE_PROCESSOR_FILE"]?.let {
                Path.of(it)
            } ?: throw IllegalStateException("No TRACE_PROCESSOR_FILE env variable present")
            val traceProcessorDestFile = Path.of(workDir.pathString, "trace_processor_shell")
            Files.copy(traceProcessorFile, traceProcessorDestFile)
            traceProcessorDestFile.toFile().setExecutable(true)
            return traceProcessorDestFile
        }
    }
}
