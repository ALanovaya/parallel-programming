package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class TreiberStackTest {
    private val stack = TreiberStack<Int>()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Operation
    fun top() = stack.top()

    @Test
    fun modelTest() = ModelCheckingOptions()
            .threads(6)
            .sequentialSpecification(SimpleStack::class.java)
            .checkObstructionFreedom()
            .check(this::class.java)

    @Test
    fun stressTest() = StressOptions()
            .threads(6)
            .sequentialSpecification(SimpleStack::class.java)
            .check(this::class)
}