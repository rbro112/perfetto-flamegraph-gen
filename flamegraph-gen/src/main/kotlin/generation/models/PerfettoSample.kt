package com.emergetools.perfetto.flamegraph.generation.data

data class PerfettoSample(
    // TODO
    val id: Int,
    val ts: Long,
    val utid: Int,
    val callsiteId: Int,
)

//"id","type","ts","utid","cpu","cpu_mode","callsite_id","unwind_error","perf_session_id","id","type","depth","parent_id","frame_id","id","type","name","mapping","rel_pc","symbol_set_id","deobfuscated_name"
data class PerfettoCallsiteFrame(
    val id: Int,
    val depth: Int,
    val parentId: Int,
)

data class PerfettoFrame(
    val ts: Long?,
    val callsiteId: Int?,
    val frameId: Int,
    val parentCallsiteId: Int?,
    val depth: Int,
    val frameName: String
)
