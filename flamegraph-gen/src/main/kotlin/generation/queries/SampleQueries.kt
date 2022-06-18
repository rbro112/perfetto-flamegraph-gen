package generation.queries

import generation.PerfettoTrace
import generation.models.PerfettoFrame
import generation.models.PerfettoSample
import generation.utils.mapResults
import generation.utils.mapResultsInParallel

/**
 * Each thread will have multiple samples depending on the duration & sampling
 * frequency. We'll query for all samples associated with a thread.
 *
 * Each sample will also have a callsite_id, which is the leaf-most frame in the stack.
 * We'll then work our way up from the leaf-most frame to the root in [assembleCallstack].
 */
fun PerfettoTrace.getSamplesForThread(utid: Int): List<PerfettoSample> {
    println("Querying samples for threadId $utid...")
    val query = """
            SELECT 
                ts,
                callsite_id
            FROM perf_sample
            LEFT JOIN stack_profile_callsite as c on perf_sample.callsite_id = c.id
            WHERE
                perf_sample.utid = $utid
                AND perf_sample.cpu_mode = "user"
            ORDER BY ts;
        """.trimIndent()

    // We assemble a callstack with a recursive SQL query for each sample, see [assembleCallstack].
    // As a performance optimization, we map the sample creation in parallel.
    return runQuery(query).mapResultsInParallel { columns ->
        val leafCallsiteId = columns[1].toIntOrNull()
        // Callstack will be in leaf -> root order after querying, so reverse to get root -> leaf
        val callstack = leafCallsiteId?.let(::assembleCallstack)?.reversed() ?: emptyList()

        PerfettoSample(
            ts = columns[0].toLong(),
            leafCallsiteId = leafCallsiteId,
            callstack = callstack,
        )
    }
}

/**
 * Each frame has a reference to its parent frame, so we can build a callstack by recursively
 * querying the up the stack until the parent is = NULL.
 */
fun PerfettoTrace.assembleCallstack(callsiteId: Int): List<PerfettoFrame> {
    val query = """
            WITH callstack AS (
                SELECT
                    c.id as callsite_id,
                    c.frame_id,
                    c.parent_id,
                    c.depth,
                    f.name as frame_name
                FROM stack_profile_callsite as c
                LEFT JOIN stack_profile_frame as f on c.frame_id = f.id
                WHERE callsite_id = $callsiteId

                UNION ALL

                SELECT
                    c.id as callsite_id,
                    c.frame_id,
                    c.parent_id,
                    c.depth,
                    f.name as frame_name
                FROM callstack, stack_profile_callsite as c
                LEFT JOIN stack_profile_frame as f on c.frame_id = f.id
                WHERE callstack.parent_id = c.id
            )
            SELECT * FROM callstack;
        """.trimIndent()

    return runQuery(query)
        .mapResults { columns ->
            PerfettoFrame(
                callsiteId = columns[0].toIntOrNull(),
                frameId = columns[1].toInt(),
                parentCallsiteId = columns[2].toIntOrNull(),
                depth = columns[3].toInt(),
                frameName = columns[4].trim('"'),
            )
        }
}
