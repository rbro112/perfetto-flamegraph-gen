package generation.models

import kotlinx.serialization.Serializable

@Serializable
data class PerfettoThread(
    val id: Int,
    val utid: Int,
    val name: String,
    val startTs: Long,
    val endTs: Long? = null,
    val processId: Int,
    val samples: List<PerfettoSample>,
) {
    val isMainThread: Boolean = id == processId
}
