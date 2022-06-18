package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoThread
import generation.utils.mapResults

fun PerfettoTrace.getAllThreadsForProcess(processName: String): List<PerfettoThread> {
    println("Querying for threads for $processName...")
    val query = """
            SELECT 
                tid,
                utid,
                thread.name as thread_name,
                thread.start_ts as start,
                thread.end_ts as end,
                pid
            FROM thread
                left join process using(upid)
            WHERE
                process.name = "$processName"; 
        """.trimIndent()

    return runQuery(query)
        .mapResults { columns ->
            val utid = columns[1].toInt()
            val samples = getSamplesForThread(utid)

            PerfettoThread(
                id = columns[0].toInt(),
                utid = utid,
                name = columns[2].trim('"'),
                startTs = columns[3].toLong(),
                endTs = columns[4].toLongOrNull(),
                processId = columns[5].toInt(),
                samples = samples,
            )
        }
}
