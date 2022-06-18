package generation.transforms

import generation.PerfettoTrace
import generation.models.PerfettoProcess

/**
 * Implement this interface to transform a [PerfettoProcess] to the output string to
 * be written to a file.
 */
interface Transformer {

    fun transform(
        trace: PerfettoTrace,
        process: PerfettoProcess,
    ): String
}
