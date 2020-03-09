package utils

import org.jetbrains.dokka.model.Module
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.plugability.UnresolvedTypeHandler
import org.jetbrains.dokka.testApi.testRunner.AbstractCoreTest

abstract class AbstractModelTest(val path: String? = null, val pkg: String) : ModelDSL(), AssertDSL {

    fun inlineModelTest(
        query: String,
        platform: String = "jvm",
        targetList: List<String> = listOf("jvm"),
        prependPackage: Boolean = true,
        typeHandler: UnresolvedTypeHandler = UnresolvedTypeHandler.Exception,
        block: Module.() -> Unit
    ) {
        val configuration = dokkaConfiguration {
            unresolvedTypeHandler = typeHandler
            passes {
                pass {
                    sourceRoots = listOf("src/")
                    analysisPlatform = platform
                    targets = targetList
                }
            }
        }
        val prepend = path.let { p -> p?.let { "|$it\n" } ?: "" } + if(prependPackage) "|package $pkg" else ""

        testInline(("$prepend\n$query").trim().trimIndent(), configuration) {
            documentablesTransformationStage = block
        }
    }


}
