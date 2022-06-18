package com.emergetools.flamegraph.gen.cli

inline fun <T> measureRuntime(block: () -> T): Pair<Long, T> {
    val startTime = System.currentTimeMillis()
    val result = block()
    val durationMs = System.currentTimeMillis() - startTime
    return Pair(durationMs, result)
}

inline fun measureRuntime(block: () -> Unit): Long = measureRuntime<Unit>(block).first
