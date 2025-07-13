// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

fun test() {
    // Test our mutable variable checker instead
    <!MUTABLE_VAR_IN_FUNCTION!>var x = 10<!>
    val y = 20
    <!VAR_ASSIGNMENT_IN_FUNCTION!>x = 30<!>
}
