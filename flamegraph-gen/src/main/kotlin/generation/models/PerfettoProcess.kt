package generation.models

import kotlinx.serialization.Serializable

@Serializable
data class PerfettoProcess(
    val id: Int,
    val name: String,
    val startTs: Long,
    val endTs: Long?,
    val threads: List<PerfettoThread>,
)
