// Main function
fun main() {
    val results1 = bench("Benchmark1", iterations = 1_000_000) {
        setup {
            println("Setting up data for Benchmark1...")
            (1..10_000).toList() // Return setup data
        }
        bench(name = "Original Sum") { list: List<Int> ->
            list.sum() // The original benchmark
        }
        bench(name = "Optimized Max") { list: List<Int> ->
            list.max() // The optimized benchmark
        }
    }

    val results2 = bench("Benchmark2", iterations = 500_000) {
        setup {
            println("Setting up data for Benchmark2...")
            (1..10_000).map { it * it } // Return setup data
        }
        bench(name = "Original Product") { list: List<Int> ->
            list.fold(1L) { acc, i -> acc * i } // A complex benchmark
        }
        bench(name = "Optimized Sum of Squares") { list: List<Int> ->
            list.sumOf { it.toLong() * it } // An optimized benchmark
        }
    }

    // Process and display results
    println("Benchmark1 Results:")
    results1.forEach { it.printToConsole() }

    println("\nBenchmark2 Results:")
    results2.forEach { it.printToConsole() }
}
