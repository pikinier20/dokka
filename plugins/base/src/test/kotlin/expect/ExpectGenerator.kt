package expect

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ExpectGenerator : ExpectUtils() {

    @Disabled
    @Test
    fun generateAll() = testDir.dirsWithFormats(formats).forEach { (p, f) ->
        generateExpect(p, f)
    }

    fun generateExpect(p: Path, outFormat: String) {
        val out = p.resolve("out/$outFormat/")
        Files.createDirectories(out)

        val ret = generateOutput(p.resolve("src"), outFormat)
        Files.list(out).forEach { it.deleteRecursively() }
        ret?.let { Files.list(it).forEach { f -> f.copyRecursively(out.resolve(f.fileName)) } }
    }
}