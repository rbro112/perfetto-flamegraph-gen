package com.rbro.flamegraph.gen.cli.subcommands

import generation.PerfettoTrace
import generation.queries.getProcess
import generation.transforms.FoldedFlamegraphTransformer
import kotlin.io.path.appendText
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

class GenerateFoldedStacks : GenerateSubcommand(
    name = "folded",
    help = "Generate a folded stacks flamegraph representation of a given process from a Perfetto trace"
) {

    override fun script() {
        // So we don't run into any issues writing a file that already exists
        output.deleteIfExists()

        val perfettoTrace = PerfettoTrace(trace, traceProcessor)
        val process = perfettoTrace.getProcess(processName, mainThreadOnly)

        val foldedResults = FoldedFlamegraphTransformer.transform(
            trace = perfettoTrace,
            process = process,
        )
        output.createFile().appendText(foldedResults)
        println("Folded stacks flamegraph written to $output")
    }
}
