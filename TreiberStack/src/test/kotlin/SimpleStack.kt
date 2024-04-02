package stack

class SimpleStack : Stack<Int>{
    private val stack = ArrayDeque<Int>()

    override fun push(value: Int) = stack.addLast(value)

    override fun pop(): Int? = stack.removeLastOrNull()

    override fun top(): Int? = stack.lastOrNull()
}