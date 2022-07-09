package generation.utils

inline fun measureRuntime(block: () -> Unit): Long = measureRuntime<Unit>(block).second

inline fun <T> measureRuntime(block: () -> T): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result = block()
    val durationMs = System.currentTimeMillis() - startTime
    return Pair(result, durationMs)
}
