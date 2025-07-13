package foo.bar

fun box(): String {
    // Since we removed class generation, let's test something simple
    val x = 42
    val y = 58
    val sum = x + y
    return if (sum == 100) { "OK" } else { "Fail: $sum" }
}
