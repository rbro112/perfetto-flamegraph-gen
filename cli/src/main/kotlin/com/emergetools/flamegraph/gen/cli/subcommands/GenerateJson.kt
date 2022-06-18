package com.emergetools.flamegraph.gen.cli.subcommands

import generation.PerfettoTrace
import generation.queries.getProcess
import generation.transforms.JsonFlamegraphTransformer
import kotlin.io.path.appendText
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

class GenerateJson : GenerateSubcommand(
    name = "json",
    help = "Generate a flamegraph JSON representation of the timestamped samples."
) {
    override fun script() {
        // So we don't run into any issues writing a file that already exists
        output.deleteIfExists()

        val perfettoTrace = PerfettoTrace(trace, traceProcessor)
        val process = perfettoTrace.getProcess(processName)

        val jsonResults = JsonFlamegraphTransformer.transform(
            trace = perfettoTrace,
            process = process,
        )
        output.createFile().appendText(jsonResults)
        println("JSON flamegraph written to $output")
    }
}
