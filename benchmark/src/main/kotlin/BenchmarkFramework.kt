import kotlin.system.measureNanoTime

// The benchmark framework for running multiple benchmarks
class BenchmarkFramework<T>(
    private val benchName: String,
    private val iterations: Int,
    private val warmupIterations: Int
) {
    private var setupBlock: (() -> T)? = null
    private val benchBlocks = mutableListOf<BenchmarkScenario<T>>()

    init {
        require(iterations > 0) { "Iterations must be greater than 0" }
        require(warmupIterations >= 0) { "Warmup iterations must not be negative" }
    }

    // Define the setup block
    fun setup(block: () -> T): BenchmarkFramework<T> {
        setupBlock = block
        return this
    }

    // Define the benchmark scenarios (multiple benchmark blocks)
    fun bench(name: String? = null, block: (T) -> Unit): BenchmarkFramework<T> {
        val scenarioName = name ?: "Scenario ${benchBlocks.size + 1}"
        benchBlocks.add(BenchmarkScenario(scenarioName, block))
        return this
    }

    // Run all benchmarks and return results
    fun run(): List<BenchmarkResult> {
        val results = mutableListOf<BenchmarkResult>()

        // Safely invoke the setup block and handle typing
        val setupData = setupBlock?.invoke()
            ?: throw IllegalStateException("Setup block must be defined before running benchmarks.")

        benchBlocks.forEach { scenario ->
            try {
                // Warm-up phase
                repeat(warmupIterations) { scenario.block(setupData) }

                // Benchmark phase
                val times = LongArray(iterations) {
                    measureNanoTime { scenario.block(setupData) }
                }

                // Compute statistics
                val averageTimeMs = times.average() / 1_000_000.0
                val medianTimeMs = times.median() / 1_000_000.0
                val stdDevTimeMs = times.stdDev() / 1_000_000.0

                results.add(
                    BenchmarkResult(
                        benchName,
                        scenario.name,
                        averageTimeMs,
                        medianTimeMs,
                        stdDevTimeMs,
                        times.toList()
                    )
                )
            } catch (e: Exception) {
                println("Error in benchmark '${scenario.name}': ${e.message}")
            }
        }

        return results
    }

    // Data class to hold each benchmark scenario
    data class BenchmarkScenario<T>(
        val name: String,
        val block: (T) -> Unit
    )
}