package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import org.jetbrains.kotlinx.lincheck.LoggingLevel

class EliminationBackoffStackTest {
    private val stack = EliminationBackoffStack<Int>()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Operation
    fun top() = stack.top()

    @Test
    fun modelTest() = ModelCheckingOptions()
        .sequentialSpecification(SimpleStack::class.java)
        .checkObstructionFreedom()
        .threads(4)
        .iterations(5)
        .invocationsPerIteration(10)
        .hangingDetectionThreshold(1000)
        .logLevel(LoggingLevel.INFO)
        .check(this::class)

    @Test
    fun stressTest() = StressOptions()
        .sequentialSpecification(SimpleStack::class.java)
        .logLevel(LoggingLevel.INFO)
        .check(this::class)
}

