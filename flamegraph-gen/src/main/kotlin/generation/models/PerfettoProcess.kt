package com.emergetools.perfetto.flamegraph.generation.data

data class PerfettoProcess(
    val id: Int,
    val name: String,
    val startTs: Long,
    val endTs: Long?,
    val threads: List<PerfettoThread>
)
