package expect

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

data class ProcessResult(val code: Int, val out: String, val err: String? = null)

fun Path.dirsWithFormats(formats: List<String>): List<Pair<Path, String>> =
    Files.list(this).toList().flatMap { p -> formats.map { p to it } }

fun Path.asString() = normalize().toString()
fun Path.deleteRecursively() = toFile().deleteRecursively()

fun Path.copyRecursively(target: Path) = toFile().copyRecursively(target.toFile())

fun Path.listRecursively(filter: (Path) -> Boolean): List<Path> = when {
        Files.isDirectory(this) -> listOfNotNull(takeIf(filter)) + Files.list(this).toList().flatMap {
            it.listRecursively(
                filter
            )
        }
        Files.isRegularFile(this) -> listOfNotNull(this.takeIf(filter))
        else -> emptyList()
    }

fun InputStream.bufferResult(): String = this.bufferedReader().lines().toList().joinToString("\n")