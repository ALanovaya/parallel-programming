import stack.EliminationBackoffStack
import stack.TreiberStack

import kotlin.random.Random

fun randomStackOperations() {
    val stack = Stack<Int>()
    val operations = listOf("push", "pop", "top")
    val range = 1..100

    repeat(1_000_000) {
        when (operations.random()) {
            "push" -> stack.push(range.random())
            "pop" -> stack.pop()
            "top" -> stack.top()
        }
    }
}

fun main() {

}