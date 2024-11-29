// The result structure for storing benchmark results
data class BenchmarkResult(
    val benchName: String,
    val scenarioName: String,
    val averageTimeMs: Double,
    val medianTimeMs: Double,
    val stdDevTimeMs: Double,
    val iterationTimesNs: List<Long>
) {

    val headerName: String
        get() = "$benchName.$scenarioName"

    fun printToConsole() {
        println(
            """
            $headerName:
                Average execution time: ${"%.3f".format(averageTimeMs)} ms
                Median execution time: ${"%.3f".format(medianTimeMs)} ms
                Std Dev execution time: ${"%.3f".format(stdDevTimeMs)} ms
            """.trimIndent()
        )
    }

    fun toCsv(): String {
        val header = "Benchmark Name,Average Time (ms),Median Time (ms),Std Dev (ms)"
        val data = "$headerName,$averageTimeMs,$medianTimeMs,$stdDevTimeMs"
        return "$header\n$data"
    }

    fun toHtml(): String {
        return """
            <table>
                <tr><th>Benchmark Name</th><th>Average Time (ms)</th><th>Median Time (ms)</th><th>Std Dev (ms)</th></tr>
                <tr><td>$headerName</td><td>${"%.3f".format(averageTimeMs)}</td>
                <td>${"%.3f".format(medianTimeMs)}</td>
                <td>${"%.3f".format(stdDevTimeMs)}</td></tr>
            </table>
        """.trimIndent()
    }
}