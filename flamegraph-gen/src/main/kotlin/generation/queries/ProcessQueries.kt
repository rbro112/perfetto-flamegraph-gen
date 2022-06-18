package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoProcess
import generation.utils.mapResults

fun PerfettoTrace.getProcess(processName: String): PerfettoProcess {
    println("Querying for process $processName...")
    val query = """
            SELECT 
                pid,
                name,
                start_ts,
                end_ts
            FROM process
            WHERE name = "$processName"; 
        """.trimIndent()

    val threads = getAllThreadsForProcess(processName)

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

fun PerfettoTrace.getProcesses(processNames: List<String>): List<PerfettoProcess> {
    return processNames.map(::getProcess)
}
