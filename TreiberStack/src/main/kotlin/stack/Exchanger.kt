package stack

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference

class Exchanger<T> {
    private val slot: AtomicStampedReference<T?> = AtomicStampedReference(null, EMPTY)

    @Throws(TimeoutException::class)
    fun exchange(y: T, timeout: Long, unit: TimeUnit): T? {
        val timeDuration = unit.toNanos(timeout)
        val timeLimit = System.nanoTime() + timeDuration
        val stamp = intArrayOf(EMPTY)

        while (System.nanoTime() < timeLimit) {
            val x = slot.get(stamp)
            when (stamp[0]) {
                EMPTY -> {
                    if (slot.compareAndSet(null, y, EMPTY, WAITING)) {
                        while (System.nanoTime() < timeLimit)
                            slot.get(stamp).let { newX ->
                                if (stamp[0] == BUSY) {
                                    slot.set(null, EMPTY)
                                    return newX
                                }
                            }
                        throw TimeoutException()
                    }
                }
                WAITING -> {
                    if (slot.compareAndSet(x, y, WAITING, BUSY))
                        return x!!
                }
                BUSY -> {}
            }
        }
        throw TimeoutException()
    }

    companion object {
        private const val EMPTY = 0
        private const val WAITING = 1
        private const val BUSY = 2
    }
}