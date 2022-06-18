package com.emergetools.perfetto.flamegraph.generation.utils

fun <T> String.mapResults(block: (String) -> T): List<T> {
    val resultLines = split("\n")

    //println(resultLines.first())

    //println("Results: $this")
    return split("\n")
        .filter { it.isNotBlank() } // drop blank lines
        .drop(1) // drop the header row
        .map(block)
}
