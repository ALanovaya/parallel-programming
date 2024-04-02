package stack
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicStampedReference
import kotlin.random.Random

class EliminationBackoffStack<T>(
    private val capacity: Int = 10,
    private val timeout: Long = 200,
) : Stack<T> {
    companion object {
        private const val EMPTY = 0
        private const val WAITING = 1
        private const val BUSY = 2
    }

    private val slotArray = Array<AtomicStampedReference<T?>>(capacity) { AtomicStampedReference(null, EMPTY) }
    private val stamp = intArrayOf(EMPTY)

    private val head: AtomicReference<Node<T>?> = AtomicReference(null)

    private fun tryPush(n: Node<T>): Boolean {
        val m = head.get()
        n.next = m
        return head.compareAndSet(m, n)
    }

    override fun push(value: T) {
        val n = Node(value)
        while (true) {
            if (tryPush(n)) return

            val i = Random(System.currentTimeMillis()).nextInt(capacity)
            val timeLimit = System.nanoTime() + timeout

            while (System.nanoTime() < timeLimit) {
                if (slotArray[i].compareAndSet(null, value, EMPTY, WAITING))
                    while (System.nanoTime() < timeLimit)
                        slotArray[i].get(stamp).let { _ ->
                            if (stamp[0] == BUSY) {
                                slotArray[i].set(null, EMPTY)
                                return
                            }
                        }
            }
         }
    }

    private fun tryPop(): Node<T>? {
        val m = head.get() ?: return null
        val n = m.next
        return if (head.compareAndSet(m, n)) m else null
    }

    override fun pop(): T? {
        while (true) {
            val n = tryPop()
            if (n != null) return n.value

            val i = Random(System.currentTimeMillis()).nextInt(capacity)
            val timeLimit = System.nanoTime() + timeout

            while (System.nanoTime() < timeLimit) {
                val x = slotArray[i].get(stamp)
                if (slotArray[i].compareAndSet(x, null, WAITING, BUSY)) return x
            }
        }
    }

    override fun top(): T? = head.get()?.value
}