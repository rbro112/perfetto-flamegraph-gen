package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoFrame
import generation.models.PerfettoSample
import generation.utils.getValueOrNull
import generation.utils.mapResults

fun PerfettoTrace.getAllFrames(processName: String): List<PerfettoFrame> {
    val query = """
        WITH callstack AS (
            SELECT
                c.id as callsite_id,
                c.frame_id,
                c.parent_id,
                c.depth,
                f.name as frame_name,
                f.deobfuscated_name,
                ps.ts as ts,
                t.tid as tid,
                p.pid as pid
            FROM stack_profile_callsite as c
            INNER JOIN stack_profile_frame as f on c.frame_id = f.id
            LEFT JOIN perf_sample as ps on ps.callsite_id = c.id
            LEFT JOIN thread as t on t.utid = ps.utid
            LEFT JOIN process as p on t.upid = p.upid
            WHERE p.name = "$processName"

            UNION ALL

            SELECT
                c.id as callsite_id,
                c.frame_id,
                c.parent_id,
                c.depth,
                f.name as frame_name,
                f.deobfuscated_name,
                ps.ts as ts,
                t.tid as tid,
                p.pid as pid
            FROM callstack, stack_profile_callsite as c
            LEFT JOIN stack_profile_frame as f on c.frame_id = f.id
            LEFT JOIN perf_sample as ps on ps.callsite_id = c.id
            LEFT JOIN thread as t on t.utid = ps.utid
            LEFT JOIN process as p on t.upid = p.upid
            WHERE callstack.parent_id = c.id
        )
        SELECT * FROM callstack;
    """.trimIndent()

    return runQuery(query).mapResults { columns ->
        PerfettoFrame(
            callsiteId = columns[0].toInt(),
            frameId = columns[1].toInt(),
            parentCallsiteId = columns[2].toIntOrNull(),
            depth = columns[3].toInt(),
            frameName = columns[4].trim('"'),
            deobfuscatedFrameName = columns.getValueOrNull(5),
            ts = columns[6].toLongOrNull(),
            tid = columns[7].toIntOrNull(),
            pid = columns[8].toIntOrNull(),
        )
    }
}

class SampleHelper(perfettoTrace: PerfettoTrace, processName: String) {

    private val frames by lazy {
        perfettoTrace.getAllFrames(processName)
    }

    private val framesByCallsiteId: Map<Int, PerfettoFrame> by lazy {
        frames.associateBy(PerfettoFrame::callsiteId)
    }

    private val callstacksByTsTid: Map<Pair<Long, Int>, List<PerfettoFrame>> by lazy {
        buildCallstacksByTsTid()
    }

    private fun buildCallstacksByTsTid(): Map<Pair<Long, Int>, List<PerfettoFrame>> {
        val leafFrames = frames.filter { it.pid != null && it.tid != null && it.ts != null }
        return leafFrames.associateBy(
            keySelector = { Pair(it.ts!!, it.tid!!) },
            valueTransform = {
                buildCallstack(framesByCallsiteId, it)
            }
        )
    }

    private fun buildCallstack(frames: Map<Int, PerfettoFrame>, frame: PerfettoFrame): List<PerfettoFrame> {
        val callstackList = mutableListOf<PerfettoFrame>()
        var parentFrameId = frame.parentCallsiteId
        while (parentFrameId != null) {
            val parentFrame = frames[parentFrameId]
            parentFrame?.let(callstackList::add)
            parentFrameId = parentFrame?.parentCallsiteId
        }
        return callstackList
    }

    fun getSamplesForThread(tid: Int): List<PerfettoSample> {
        val timestamps = callstacksByTsTid.keys.map { it.first }.sorted()
        return timestamps.mapNotNull { timestamp ->
            val key = Pair(timestamp, tid)
            val callstack = callstacksByTsTid[key]?.sortedBy { it.depth } ?: emptyList()
            if (callstack.isEmpty()) return@mapNotNull null
            PerfettoSample(
                ts = timestamp,
                callstack = callstack,
                leafCallsiteId = 0
            )
        }
    }
}
