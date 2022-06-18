package com.emergetools.flamegraph.gen.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import generation.PerfettoTrace
import generation.queries.getProcess
import generation.transforms.FoldedFlamegraphTransformer
import java.nio.file.Files
import kotlin.io.path.appendText
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists

// TODO: One for Json, one for Folded
fun main(args: Array<String>) {
    GenerateFoldedStacks()
        .main(args)
}

// TODO: Rename to generate folded
class GenerateFoldedStacks : CliktCommand() {

    // TODO: Perfetto trace file (required)
    private val trace by argument()
        .path(mustExist = true, canBeDir = false, mustBeReadable = true)

    // TODO: Ouput folder (optional) - name separate?
    private val output by argument()
        .path(mustExist = false, canBeDir = false)

    // TODO: Process name (required)

    // TODO: Thread id/names (optional - will return all threads)

    // TODO: Proguard mapping path (optional)

    // TODO: Start timestamp (optional)
    // TODO: End timestamp (optional)

    // TODO: Binary to perfetto trace processor
    private val proguardMapping by option()

    // TODO
    override fun run() {
        val perfettoTrace = PerfettoTrace(trace)
        output.deleteIfExists()

        // TODO: Deobfuscate
        // TODO: Symbolize

        // TODO: Get process
        val process = perfettoTrace.getProcess("com.emerge.hackernews")


        val foldedResults = FoldedFlamegraphTransformer.transform(perfettoTrace, process)
        createTempFile("results", ".folded").apply {
            appendText(foldedResults)
            Files.copy(this, output)
            deleteExisting()
        }
    }
}
