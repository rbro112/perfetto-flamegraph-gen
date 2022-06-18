package com.emergetools.perfetto.flamegraph.generation.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

/**
 * Helper to run a map transform with coroutines.
 * Each item will be transformed in its own coroutine via [async], and a list of transformed items
 * will be returned once all transformations complete.
 *
 * Similar to [mapAsync], but the transformations are automatically awaited and returned as a normal list,
 * with no intermediary [Deferred] objects.
 */
fun <T, R> Sequence<T>.mapInParallel(transform: suspend (T) -> R): List<R> {
    return mapAsync(transform).awaitAllBlocking()
}

/**
 * Helper to run a map transform with coroutines.
 * Each item will be transformed in its own coroutine via [async], and a list of transformed items
 * will be returned once all transformations complete.
 *
 * Similar to [mapAsync], but the transformations are automatically awaited and returned as a normal list,
 * with no intermediary [Deferred] objects.
 */
fun <T, R> Iterable<T>.mapInParallel(transform: suspend (T) -> R): List<R> {
    return mapAsync(transform).awaitAllBlocking()
}

/**
 * Helper to run a map transform with coroutines.
 * Each item will be transformed in its own coroutine via [async].
 *
 * See [awaitAllBlocking] to easily await the results.
 */
fun <T, R> Sequence<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> {
    return map {
        GlobalScope.async { transform(it) }
    }.toList()
}

/**
 * Helper to run a map transform with coroutines.
 * Each item will be transformed in its own coroutine via [async].
 *
 * See [awaitAllBlocking] to easily await the results.
 */
fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> {
    return map {
        GlobalScope.async { transform(it) }
    }
}

fun <T> Sequence<T>.forEachInParallel(block: suspend (T) -> Unit) {
    mapAsync(block).awaitAllBlocking()
}

fun <T> Iterable<T>.forEachInParallel(block: suspend (T) -> Unit) {
    mapAsync(block).awaitAllBlocking()
}

/**
 * Helper to synchronously wait for all deferred items to complete.
 */
fun <T> Sequence<Deferred<T>>.awaitAllBlocking(): List<T> {
    return runBlocking { toList().awaitAll() }
}

/**
 * Helper to synchronously wait for all deferred items to complete.
 */
fun <T> List<Deferred<T>>.awaitAllBlocking(): List<T> {
    return runBlocking { awaitAll() }
}
