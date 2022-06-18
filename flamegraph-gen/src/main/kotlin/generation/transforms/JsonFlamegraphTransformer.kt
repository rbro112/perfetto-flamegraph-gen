package generation.transforms

import generation.PerfettoTrace
import generation.models.PerfettoProcess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonFlamegraphTransformer : Transformer {

    override fun transform(trace: PerfettoTrace, process: PerfettoProcess): String {
        println("Fetched process, converting to flamegraph JSON...")
        return Json.encodeToString(process)
    }
}
