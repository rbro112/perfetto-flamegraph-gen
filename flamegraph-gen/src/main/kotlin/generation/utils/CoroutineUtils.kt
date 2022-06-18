package generation.utils

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
fun <T, R> Iterable<T>.mapInParallel(transform: suspend (T) -> R): List<R> {
    return mapAsync(transform).awaitAllBlocking()
}

fun <T> Iterable<T>.forEachInParallel(block: suspend (T) -> Unit) {
    mapAsync(block).awaitAllBlocking()
}

/**
 * Helper to run a map transform with coroutines.
 * Each item will be transformed in its own coroutine via [async].
 */
fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> {
    return map {
        GlobalScope.async { transform(it) }
    }
}

/**
 * Helper to synchronously wait for all deferred items to complete.
 */
fun <T> List<Deferred<T>>.awaitAllBlocking(): List<T> {
    return runBlocking { awaitAll() }
}
