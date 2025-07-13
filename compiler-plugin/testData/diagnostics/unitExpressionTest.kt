// RUN_PIPELINE_TILL: FRONTEND

fun println(message: Any?): Unit {}

fun sideEffectFunction(): Unit {
    // This should be allowed - it's a normal Unit function
}

fun testFunction() {
    // This should trigger an error - calling a Unit-returning function
    <!UNIT_EXPRESSION_IN_FUNCTION!>println("Hello World")<!>
    
    // This should also trigger an error
    <!UNIT_EXPRESSION_IN_FUNCTION!>sideEffectFunction()<!>
    
    // Pure expressions should be fine
    val x = 42
    val y = x + 1
} 