package com.rbro.flamegraph.gen.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import generation.utils.measureRuntime

abstract class GenerateSubcommand(
    name: String,
    help: String,
) : CliktCommand(
    name = name,
    help = help,
) {

    protected val trace by option("--trace", help = "Perfetto trace file")
        .path(mustExist = true, canBeDir = false, mustBeReadable = true)
        .required()

    protected val output by option("--output", help = "Output file")
        .path(mustExist = false, canBeDir = false)
        .required()

    protected val processName by option(
        "--process",
        help = "The name of the process to create a flamegraph for all sampled threads"
    ).required()

    protected val mainThreadOnly: Boolean by option(
        "--main-thread-only",
        help = "Only generate samples for the main thread",
    ).flag()

    protected val traceProcessor by option("--trace-processor", help = "Output file")
        .path(mustExist = true, canBeDir = false)

    abstract fun script()

    override fun run() {
        val runtime = measureRuntime(::script)
        println("Completed in $runtime ms")
    }
}
