package generation.transforms

import generation.PerfettoTrace
import generation.models.PerfettoFrame
import generation.models.PerfettoProcess
import generation.models.PerfettoSample
import generation.models.PerfettoThread
import generation.queries.getAllFrames

object FoldedFlamegraphTransformer : Transformer {

    override fun transform(
        trace: PerfettoTrace,
        process: PerfettoProcess,
    ): String {
        return buildString {
            process.threads.forEach { thread ->
                println("Fetched thread ${thread.name}, converting to folded stacks...")
                if (thread.samples.isEmpty()) return@forEach

                val sortedSamples = thread.samples.sortedBy(PerfettoSample::ts)

                val timeDiffs = sortedSamples.mapIndexedNotNull { index, sample ->
                    if (index == 0) null else sample.ts - sortedSamples.elementAt(index - 1).ts
                }.toMutableList().apply {
                    // Last sample will not have a time diff associated with it, so we need to add it manually
                    // We don't want to assume a fixed frequency, so take the average of last 10 time diffs as an estimate
                    val averageTimeBetweenLastSamples = takeLast(10).average().toLong()
                    add(averageTimeBetweenLastSamples)
                }

                val folded = timeDiffs.mapIndexedNotNull { index, timeDiff ->
                    val stack = sortedSamples.elementAtOrNull(index)
                        ?.callstack
                        ?.joinToString(";", transform = PerfettoFrame::frameName)
                        ?: return@mapIndexedNotNull null

                    // Main thread is a special case that we want to keep name the thread the process
                    val threadName = if (thread.isMainThread) process.name else thread.name

                    "$threadName;$stack $timeDiff"
                }.joinToString("\n")

                append(folded)
            }
        }
//
    }

    fun samplesForThread(thread: PerfettoThread, frames: List<PerfettoFrame>): List<PerfettoSample> {
        val framesByCallsiteId = frames.associateBy { it.callsiteId }
        val leafFrames = frames.filter { it.pid != null && it.tid != null && it.ts != null }
        val tsTidCallstackMap: MutableMap<Pair<Long, Int>, MutableList<PerfettoFrame>> = leafFrames.associateBy(
            keySelector = { Pair(it.ts!!, it.tid!!) },
            valueTransform = { mutableListOf(it) }
        ).toMutableMap()

        tsTidCallstackMap.values.forEach { value ->
            val callstack = buildCallstack(framesByCallsiteId, value.first())
            value.addAll(callstack)
        }

        val timestamps = tsTidCallstackMap.keys.map { it.first }.sorted()
        return timestamps.mapNotNull { timestamp ->
            val key = Pair(timestamp, thread.id)
            val callstack = tsTidCallstackMap[key]?.sortedBy { it.depth } ?: emptyList()
            if (callstack.isEmpty()) return@mapNotNull null
            PerfettoSample(
                ts = timestamp,
                callstack = callstack,
                leafCallsiteId = 0
            )
        }
    }

    // TODO: Recurse frames?
    fun buildCallstack(frames: Map<Int, PerfettoFrame>, frame: PerfettoFrame): List<PerfettoFrame> {
        val callstackList = mutableListOf<PerfettoFrame>()
        var parentFrameId = frame.parentCallsiteId
        while (parentFrameId != null) {
            val parentFrame = frames[parentFrameId]
            parentFrame?.let(callstackList::add)
            parentFrameId = parentFrame?.parentCallsiteId
        }
        return callstackList
    }
}
