# Perfetto Flamegraph Generation

Helper for generating callstack flamegraphs from Perfetto traces.

## Prerequisites

#### Tracing on Android 12+

`linux.perf` events can only be sampled on Android
12+ ([source](https://github.com/google/perfetto/issues/177#issuecomment-926858870)).

#### Sampling enabled for Perfetto

Enable and configure in your Perfetto config with:

```prototext
...
data_sources {
  config {
    name: "linux.perf"
    sampling_frequency: < hz >
    target_cmdline: < package names >
  }
}
```

#### Perfetto trace processor downloaded locally

Perfetto-flamegraph-generation relies on the perfetto `trace_processor`. We hope to package the binaries in the future
for specific architectures,
but for now you will need to set the `PERFETTO_TRACE_PROCESSOR` environment variable to the path of
the `trace_processor` binary.

```bash
export PERFETTO_TRACE_PROCESSOR=<path to trace_processor>
```

Alternatively you can pass the path to the `trace_processor` binary to the cli with the `--trace-processor` option.

Prebuilts can be found at the corresponding links found in the [Perfetto repository](
From https://github.com/google/perfetto/blob/master/tools/trace_processor).

## CLI

Download the jar from the releases and run:

```bash
java -jar perfetto-flamegraph-gen.jar [folded|json] --trace <trace_path> --output <output_path> --process <process_name>
```

Alternatively we package an executable which can be run directly:

```bash
./flamegraph-gen [folded|json] --trace <trace_path> --output <output_path> --process <process_name>
```

### Output types

Specify the output type as the first argument to the cli.

#### Folded

Following Brendan Gregg's [Folded stacks format](https://github.com/brendangregg/FlameGraph#2-fold-stacks).

Each row is a sample, following a pattern of:

```
thread_name;frame1;frame2;...; count (time in nanos)
```

Example folded output:

```txt
com.myapp;main; 100000
com.myapp;main;function1 100000
com.myapp;main;function1;calledFunction 100000
...
thread2;main; 100000
thread2;main;function1 100000
```

Use Brendan Gregg's [scripts](https://github.com/brendangregg/FlameGraph#3-flamegraphpl) to convert to SVGs or other
representations.

#### JSON

Data is grouped by Process -> [Threads] -> [Samples] -> [Callstack (ordered root->leaf)].

Sample:

```json
{
  "id": 123,
  "name": "com.myapp",
  ...
  "threads": [
    {
      "name": "main",
      "tid": 123,
      ...
      "samples": [
        {
          "ts": 123,
          "callstack": [
            {
              "frameName": "main",
              ...
            }
          ]
        }
      ]
    }
  ]
}
```

#### Other formats

Interested in other formats? Open a PR with the [Transformer]() implementation.

### Options

| Option            | Value                                                                                                   | Required? |
|-------------------|---------------------------------------------------------------------------------------------------------|-----------|
| --trace           | Path to the perfetto trace file.                                                                        | Yes       |
| --output          | Path to the desired output file.                                                                        | Yes       |
| --process         | Name of the process to generate a flamegraph for all threads.                                           | Yes       |
| --trace-processor | Path to the `trace_processor` binary. Will use `PERFETTO_TRACE_PROCESSOR` env variable if none provided | No        |

### Proguard mapping & symbolication

TODO

## Library

We hope to package `flamegraph-gen` as a standalone library in the near future. Open an issue if you need it sooner!

## Future improvements

- [] Package `trace_processor` into the `perfetto-flamegraph-gen` jar/executable.
- [] Package `flamegraph-gen` into standalone library.
- [] Improve runtime performance.
- [] Add support for multiple processes.
- [] Add support for specific thread samples.
- [] Add support for error stream from `trace_processor`.
