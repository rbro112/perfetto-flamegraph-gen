package generation.transforms

import generation.PerfettoTrace
import generation.models.PerfettoFrame
import generation.models.PerfettoProcess
import generation.models.PerfettoSample

object FoldedFlamegraphTransformer : Transformer {

    override fun transform(
        trace: PerfettoTrace,
        process: PerfettoProcess,
    ): String {
        return buildString {
            println("Fetched process, converting to folded stacks...")
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
    }
}
