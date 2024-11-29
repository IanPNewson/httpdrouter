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