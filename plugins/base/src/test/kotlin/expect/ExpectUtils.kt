package expect

import org.junit.jupiter.api.Assertions.assertTrue
import testApi.testRunner.AbstractCoreTest
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

open class ExpectUtils : AbstractCoreTest() {
    val testDir = Paths.get("src/test", "resources", "expect")
    val formats = listOf("html")

    protected fun generateOutput(path: Path, outFormat: String): Path? {
        val config = dokkaConfiguration {
            format = outFormat
            passes {
                pass {
                    sourceRoots = listOf(path.asString())
                }
            }
        }

        var result: Path? = null
        testFromData(config, cleanupOutput = false) {
            renderingStage = { _, context -> result = Paths.get(context.configuration.outputDir) }
        }
        return result
    }

    protected fun compareOutput(expected: Path, obtained: Path?, gitTimeout: Long = 500) {
        obtained?.let { path ->
            val gitCompare = ProcessBuilder(
                "git",
                "--no-pager",
                "diff",
                expected.asString(),
                path.asString()
            ).also { logger.info("git diff command: ${it.command().joinToString(" ")}") }
                .start()

            assertTrue(gitCompare.waitFor(gitTimeout, TimeUnit.MILLISECONDS)) { "Git timed out after $gitTimeout" }
            gitCompare.inputStream.bufferedReader().lines().forEach { logger.info(it) }
            gitCompare.errorStream.bufferedReader().lines().forEach { logger.info(it) }
            assertTrue(gitCompare.exitValue() == 0) { "${path.fileName}: outputs don't match" }
        } ?: throw AssertionError("obtained path is null")
    }

    fun Path.dirsWithFormats(formats: List<String>): List<Pair<Path, String>> = Files.list(this).toList().flatMap{ p -> formats.map { p to it }}

    fun Path.asString() = toAbsolutePath().normalize().toString()
    fun Path.deleteRecursively() = toFile().deleteRecursively()
    fun Path.copyRecursively(target: Path) = toFile().copyRecursively(target.toFile())
}