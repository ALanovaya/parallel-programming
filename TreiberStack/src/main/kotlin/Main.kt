import stack.EliminationBackoffStack

import kotlin.random.Random

fun main() {
    val stack = EliminationBackoffStack<Int>()
    val random = Random(System.nanoTime())
    val threadsNum = listOf(1, 2, 4, 6, 8)

    for(threadNum in threadsNum) {
        val threads = List(threadNum) {
            Thread {
                repeat(10_000_000) {
                    when (random.nextInt(2)) {
                        0 -> stack.push(random.nextInt(100))
                        1 -> stack.pop()
                    }
                }
            }
        }
        val start = System.currentTimeMillis()
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        println(System.currentTimeMillis() - start)
    }
}