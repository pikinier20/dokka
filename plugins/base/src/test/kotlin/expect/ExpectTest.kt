package expect

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path

class ExpectTest : ExpectUtils() {

    @TestFactory
    fun expectTest() = testDir.dirsWithFormats(formats).map { (p, f) ->
        dynamicTest(p.fileName.toString()) { testOutput(p, f) }
    }

    private fun testOutput(p: Path, outFormat: String) {
        val expectOut = p.resolve("out/$outFormat")
        val testOut = generateOutput(p.resolve("src"), outFormat)
            .also { logger.info("Test out: ${it?.asString()}") }

        compareOutput(expectOut, testOut)
        testOut?.deleteRecursively()
    }

}