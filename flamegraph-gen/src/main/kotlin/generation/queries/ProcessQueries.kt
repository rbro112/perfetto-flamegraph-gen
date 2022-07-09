package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoProcess
import generation.utils.mapResults

fun PerfettoTrace.getProcess(
    processName: String,
    mainThreadOnly: Boolean,
): PerfettoProcess {
    println("Querying for process $processName...")
    val query = """
            SELECT 
                pid,
                name,
                start_ts,
                end_ts,
                upid
            FROM process
            WHERE name = "$processName"; 
        """.trimIndent()

    val sampleHelper = SampleHelper(this, processName)
    val threads = if (mainThreadOnly) {
        listOf(getMainThreadForProcess(processName, sampleHelper))
    } else getAllThreadsForProcess(processName, sampleHelper)

    return runQuery(query)
        .mapResults { columns ->
            PerfettoProcess(
                id = columns[0].toInt(),
                name = columns[1].trim('"'),
                startTs = columns[2].toLong(),
                endTs = columns[3].toLongOrNull(),
                threads = threads,
            )
        }.first()
}
