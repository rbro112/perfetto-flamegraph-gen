package generation.utils

fun <T> String.mapResultsInParallel(block: (List<String>) -> T): List<T> {
    return split("\n")
        .filter { it.isNotBlank() } // drop blank lines
        .drop(1) // drop the header row
        .mapInParallel { block(it.split(",")) } // Results will be in csv format
}

fun <T> String.mapResults(block: (List<String>) -> T): List<T> {
    return split("\n")
        .filter { it.isNotBlank() }
        .drop(1)
        .map { block(it.split(",")) }
}
