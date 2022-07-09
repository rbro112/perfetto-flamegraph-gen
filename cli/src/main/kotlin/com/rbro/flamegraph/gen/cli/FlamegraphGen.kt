package com.rbro.flamegraph.gen.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.rbro.flamegraph.gen.cli.subcommands.GenerateFoldedStacks
import com.rbro.flamegraph.gen.cli.subcommands.GenerateJson

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
