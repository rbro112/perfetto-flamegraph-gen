package generation

import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.writeText

class PerfettoTrace(
    private val path: Path,
    private val traceProcessorPath: Path?,
) {

    private val executable by lazy {
        getPerfettoTraceProcessor(traceProcessorPath)
    }

    // TODO: Helper to determine if has perf sampling enabled or not

    /**
     * Results will be in csv format, with the first line being the header/columns
     * and the remaining lines being the data.
     */
    fun runQuery(queryString: String): String {
        val queryFile = createTempFile("trace_processor_query.sql")
        try {
            queryFile.writeText(queryString)
            return runQuery(queryFile)
        } finally {
            queryFile.deleteExisting()
        }
    }

    fun runQuery(queryFile: Path): String {
        val process = ProcessBuilder(
            executable.absolutePathString(),
            "--query-file",
            queryFile.absolutePathString(),
            path.absolutePathString(),
        ).start()
        // TODO: Handle err stream.
        return process.inputStream.readBytes().toString(Charset.defaultCharset())
    }

    companion object {

        fun getPerfettoTraceProcessor(traceProcessorPath: Path?): Path {
            val traceProcessorFile = traceProcessorPath ?: System.getenv()["TRACE_PROCESSOR_FILE"]?.let {
                Path.of(it)
            } ?: throw IllegalStateException("No TRACE_PROCESSOR_FILE env variable present")

            return traceProcessorFile
        }
    }
}
