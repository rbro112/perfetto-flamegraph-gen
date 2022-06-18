package com.emergetools.perfetto.flamegraph.generation.data

data class PerfettoThread(
    val id: Int,
    val utid: Int,
    val name: String,
    val startTs: Long,
    val endTs: Long? = null,
    val processId: Int,
) {
    val isMainThread: Boolean
        get() = id == processId
}
