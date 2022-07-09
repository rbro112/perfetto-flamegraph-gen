package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoThread
import generation.utils.mapResults

fun PerfettoTrace.getAllThreadsForProcess(processName: String, sampleHelper: SampleHelper): List<PerfettoThread> {
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
            val samples = sampleHelper.getSamplesForThread(columns[0].toInt())

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

fun PerfettoTrace.getMainThreadForProcess(processName: String, sampleHelper: SampleHelper): PerfettoThread {
    println("Querying for main thread for $processName...")
    val query = """
            SELECT 
                tid,
                utid,
                thread.name as thread_name,
                thread.start_ts as start,
                thread.end_ts as end,
                pid,
                upid
            FROM thread
                left join process using(upid)
            WHERE
                process.name = "$processName"
            AND thread.tid = process.pid;
        """.trimIndent()

    return runQuery(query)
        .mapResults { columns ->
            val utid = columns[1].toInt()
            val samples = sampleHelper.getSamplesForThread(columns[0].toInt())

            PerfettoThread(
                id = columns[0].toInt(),
                utid = utid,
                name = columns[2].trim('"'),
                startTs = columns[3].toLong(),
                endTs = columns[4].toLongOrNull(),
                processId = columns[5].toInt(),
                samples = samples,
            )
        }.first()
}
