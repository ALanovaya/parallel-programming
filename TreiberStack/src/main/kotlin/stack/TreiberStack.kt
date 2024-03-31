package stack

import stack.Stack
import stack.Node
import java.util.concurrent.atomic.AtomicReference

class TreiberStack<T> : Stack<T> {
    private var head = AtomicReference<Node<T>?>()
    override fun push(value: T) {
        val newHead = Node(value)
        while (true) {
            val lastHead = head.get()
            newHead.next = lastHead
            if (head.compareAndSet(lastHead, newHead)) {
                return
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            val lastHead = head.get() ?: return null
            if (head.compareAndSet(lastHead, lastHead.next)) {
                return lastHead.value
            }
        }
    }

    override fun top(): T? = head.get()?.value
}