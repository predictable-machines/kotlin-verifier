// RUN_PIPELINE_TILL: FRONTEND

fun printMessage(): Unit {}

fun simpleUnitTest() {
    <!UNIT_EXPRESSION_IN_FUNCTION!>printMessage()<!>
} 