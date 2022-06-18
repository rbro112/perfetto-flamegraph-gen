package com.emergetools.flamegraph.gen.cli.subcommands

import com.github.ajalt.clikt.parameters.options.option
import generation.PerfettoTrace

// TODO: Rename to generate folded
class GenerateFlamegraphJson : GenerateSubcommand(
    name = "json",
    help = "Generate a flamegraph JSON representation of the timestamped samples."
) {

    // TODO: Process Id/names (required)

    // TODO: Thread id/names (optional - will return all threads)

    // TODO: Proguard mapping path (optional)
    // TODO: Start timestamp (optional)
    // TODO: End timestamp (optional)

    // TODO: Binary to perfetto trace processor
    private val proguardMapping by option()

    // TODO
    override fun run() {
        val perfettoTrace = PerfettoTrace(trace, traceProcessorPath = traceProcessor)


        // TODO
    }
}
