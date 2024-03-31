package stack

import java.util.concurrent.Exchanger
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.random.Random

// Elimination array provides a list of exchangers which
// are picked at random for a given value.

class EliminationArray<T>(
    capacity: Int,
    private val timeout: Long,
    private val unit: TimeUnit
) {
    private val exchangers: Array<Exchanger<T?>> = Array(capacity) { Exchanger<T?>() }
    private val random = Random(System.currentTimeMillis())

    // Try exchanging value on a random exchanger.
    @Throws(TimeoutException::class)
    fun visit(value: T?): T? {
        val i = random.nextInt(exchangers.size)
        return exchangers[i].exchange(value, timeout, unit)
    }
}