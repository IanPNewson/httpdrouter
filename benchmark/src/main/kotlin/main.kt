import kotlin.system.measureNanoTime

// The result structure for storing benchmark results
data class BenchmarkResult(
    val benchName: String,
    val scenarioName: String,
    val averageTimeMs: Double,
    val iterationTimesNs: List<Long>
) {

    fun headerName() = "${benchName}.$scenarioName"

    fun printToConsole() {
        println("${headerName()}: Average execution time: ${"%.3f".format(averageTimeMs)} ms")
    }

    fun toCsv(): String {
        val header = "Benchmark Name,Average Time (ms)"
        val data = "${headerName()},$averageTimeMs"
        return "$header\n$data"
    }

    fun toHtml(): String {
        return """
            <table>
                <tr><th>Benchmark Name</th><th>Average Time (ms)</th></tr>
                <tr><td>${headerName()}</td><td>${"%.3f".format(averageTimeMs)}</td></tr>
            </table>
        """.trimIndent()
    }
}

// The benchmark framework for running multiple benchmarks
class BenchmarkFramework<T>(private val benchName: String, private val iterations: Int, private val warmupIterations: Int) {

    private var setupBlock: (() -> T)? = null
    private val benchBlocks = mutableListOf<BenchmarkScenario<T>>()

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
            // Warm-up phase
            repeat(warmupIterations) { scenario.block(setupData) }

            // Benchmark phase
            val times = LongArray(iterations) {
                measureNanoTime { scenario.block(setupData) }
            }

            // Compute results
            val averageTime = times.average() / 1_000_000.0 // Convert to milliseconds
            results.add(BenchmarkResult(benchName, scenario.name, averageTime, times.toList()))
        }

        return results
    }

    // Data class to hold each benchmark scenario
    data class BenchmarkScenario<T>(
        val name: String,
        val block: (T) -> Unit
    )
}

// Top-level function for defining a benchmark with setup and multiple scenarios
fun <T> bench(
    name: String,
    iterations: Int,
    warmupIterations: Int = 100,
    block: BenchmarkFramework<T>.() -> Unit
): List<BenchmarkResult> {
    val framework = BenchmarkFramework<T>(name, iterations, warmupIterations)
    framework.apply(block)
    return framework.run()
}

// Main function
fun main() {
    val results1 = bench("Benchmark1", iterations = 1_000_000) {
        // Setup for Benchmark1
        setup {
            println("Setting up data for Benchmark1...")
            (1..10_000).toList() // Return setup data
        }
        // First benchmark scenario
        bench(name = "Original Sum") { list: List<Int> ->
            list.sum() // The original benchmark
        }
        // Second benchmark scenario
        bench(name = "Optimized Max") { list: List<Int> ->
            list.max() // The optimized benchmark
        }
    }

    val results2 = bench("Benchmark2", iterations = 500_000) {
        // Setup for Benchmark2
        setup {
            println("Setting up data for Benchmark2...")
            (1..10_000).map { it * it } // Return setup data
        }
        // First benchmark scenario
        bench(name = "Original Product") { list: List<Int> ->
            list.fold(1L) { acc, i -> acc * i } // A complex benchmark
        }
        // Second benchmark scenario
        bench(name = "Optimized Sum of Squares") { list: List<Int> ->
            list.sumOf { it.toLong() * it } // An optimized benchmark
        }
    }

    // Process and display results
    println("Benchmark1 Results:")
    results1.forEach { it.printToConsole() }

    println("\nBenchmark2 Results:")
    results2.forEach { it.printToConsole() }

    // Example CSV or HTML export
    val csvOutput = results1.joinToString("\n") { it.toCsv() }
    println("\nCSV Output for Benchmark1:\n$csvOutput")

    val htmlOutput = results1.joinToString("\n") { it.toHtml() }
    println("\nHTML Output for Benchmark1:\n$htmlOutput")
}
