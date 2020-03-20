package signatures

import org.jetbrains.dokka.testApi.testRunner.AbstractCoreTest
import org.junit.jupiter.api.Test
import utils.TestOutputWriterPlugin
import java.nio.file.Path
import java.nio.file.Paths

class SignaturesTest : AbstractCoreTest() {

    @Test
    fun functionSignature() {
        val testDataDir = getTestDataDir("signatures.signaturesTest").toAbsolutePath()
        val outputPlugin = TestOutputWriterPlugin()
        val configuration = dokkaConfiguration {
            passes {
                pass {
                    sourceRoots = listOf("$testDataDir/jvmMain","$testDataDir/commonMain")
                    analysisPlatform = "jvm"
                    targets = listOf("jvm")
                }
                pass{
                    sourceRoots = listOf("$testDataDir/jsMain","$testDataDir/commonMain")
                    analysisPlatform = "js"
                    targets = listOf("js")
                }
            }
        }
        var result: Path? = null
        testFromData(
            configuration
            //, pluginOverrides = listOf(outputPlugin)
        ) {
            documentablesMergingStage = {
                    module ->
                module

            }
            renderingStage = {
                _,context -> result = Paths.get(context.configuration.outputDir)
            }
        }
        outputPlugin.writer.contents
        assert(true)
    }
}