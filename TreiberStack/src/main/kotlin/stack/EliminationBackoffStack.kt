package stack

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit
import java.util.EmptyStackException
import java.util.concurrent.TimeoutException

class EliminationBackoffStack<T> {
    private val top: AtomicReference<Node<T>?> = AtomicReference(null)
    private val eliminationArray: EliminationArray<T> = EliminationArray(CAPACITY, TIMEOUT, UNIT)

    companion object {
        const val CAPACITY = 100
        const val TIMEOUT: Long = 10
        val UNIT: TimeUnit = TimeUnit.MILLISECONDS
    }

    fun push(x: T) {
        val n = Node(x)
        while (true) {
            if (tryPush(n)) return
            try {
                val y = eliminationArray.visit(x) ?: return
            } catch (_: TimeoutException) {}
        }
    }

    @Throws(EmptyStackException::class)
    fun pop(): T {
        while (true) {
            val n = tryPop()
            if (n != null) return n.value
            try {
                val y = eliminationArray.visit(null)
                if (y != null) return y
            } catch (_: TimeoutException) {}
        }
    }

    protected fun tryPush(n: Node<T>): Boolean {
        val m = top.get()
        n.next = m
        return top.compareAndSet(m, n)
    }

    @Throws(EmptyStackException::class)
    protected fun tryPop(): Node<T>? {
        val m = top.get() ?: throw EmptyStackException()
        val n = m.next
        return if (top.compareAndSet(m, n)) m else null
    }
}