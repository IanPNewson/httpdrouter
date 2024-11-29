import kotlin.math.sqrt

// Extension functions to calculate statistics
fun LongArray.median(): Double {
    if (isEmpty()) return 0.0
    val sorted = sorted()
    return if (size % 2 == 0) {
        (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
    } else {
        sorted[size / 2].toDouble()
    }
}

fun LongArray.stdDev(): Double {
    if (size <= 1) return 0.0
    val mean = average()
    return sqrt(map { (it - mean) * (it - mean) }.average())
}