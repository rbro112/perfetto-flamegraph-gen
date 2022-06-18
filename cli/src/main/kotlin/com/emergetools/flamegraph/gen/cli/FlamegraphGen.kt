package com.emergetools.flamegraph.gen.cli

import com.emergetools.flamegraph.gen.cli.subcommands.GenerateJson
import com.emergetools.flamegraph.gen.cli.subcommands.GenerateFoldedStacks
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = FlamegraphGen()
    .subcommands(
        GenerateFoldedStacks(),
        GenerateJson(),
    )
    .main(args)

class FlamegraphGen : CliktCommand(
    name = "flamegraph-gen",
    help = "Generate flamegraphs from a Perfetto trace."
) {
    override fun run() = Unit
}
