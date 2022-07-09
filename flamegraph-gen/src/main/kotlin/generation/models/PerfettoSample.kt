package generation.models

import kotlinx.serialization.Serializable

@Serializable
data class PerfettoSample(
    val ts: Long,
    val leafCallsiteId: Int?,
    val callstack: List<PerfettoFrame>
)

@Serializable
data class PerfettoFrame(
    val callsiteId: Int,
    val frameId: Int,
    val parentCallsiteId: Int?,
    val depth: Int,
    val frameName: String,
    val deobfuscatedFrameName: String?,
    val ts: Long?,
    val tid: Int?,
    val pid: Int?,
) {
    val name: String
        get() = deobfuscatedFrameName ?: frameName
}
