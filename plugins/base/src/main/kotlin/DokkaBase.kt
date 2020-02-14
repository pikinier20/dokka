package org.jetbrains.dokka.base

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.renderers.FileWriter
import org.jetbrains.dokka.base.renderers.OutputWriter
import org.jetbrains.dokka.base.renderers.html.*
import org.jetbrains.dokka.base.resolvers.DefaultLocationProviderFactory
import org.jetbrains.dokka.base.resolvers.LocationProviderFactory
import org.jetbrains.dokka.base.transformers.documentables.DefaultDocumentableMerger
import org.jetbrains.dokka.base.transformers.pages.comments.CommentsToContentConverter
import org.jetbrains.dokka.base.transformers.pages.comments.DocTagToContentConverter
import org.jetbrains.dokka.base.transformers.pages.merger.FallbackPageMergerStrategy
import org.jetbrains.dokka.base.transformers.pages.merger.PageMerger
import org.jetbrains.dokka.base.transformers.pages.merger.PageMergerStrategy
import org.jetbrains.dokka.base.transformers.pages.merger.SameMethodNamePageMergerStrategy
import org.jetbrains.dokka.base.transformers.psi.DefaultPsiToDocumentableTranslator
import org.jetbrains.dokka.base.translators.descriptors.DefaultDescriptorToDocumentableTranslator
import org.jetbrains.dokka.base.translators.documentables.DefaultDocumentableToPageTranslator
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.pages.PageTransformer

class DokkaBase : DokkaPlugin() {
    val pageMergerStrategy by extensionPoint<PageMergerStrategy>()
    val commentsToContentConverter by extensionPoint<CommentsToContentConverter>()
    val locationProviderFactory by extensionPoint<LocationProviderFactory>()
    val outputWriter by extensionPoint<OutputWriter>()
    val htmlPreprocessors by extensionPoint<PageTransformer>()

    val descriptorToDocumentableTranslator by extending(isFallback = true) {
        CoreExtensions.descriptorToDocumentableTranslator providing ::DefaultDescriptorToDocumentableTranslator
    }

    val psiToDocumentableTranslator by extending(isFallback = true) {
        CoreExtensions.psiToDocumentableTranslator with DefaultPsiToDocumentableTranslator
    }

    val documentableMerger by extending(isFallback = true) {
        CoreExtensions.documentableMerger with DefaultDocumentableMerger
    }

    val documentableToPageTranslator by extending(isFallback = true) {
        CoreExtensions.documentableToPageTranslator providing { ctx ->
            DefaultDocumentableToPageTranslator(ctx.single(commentsToContentConverter), ctx.logger)
        }
    }

    val docTagToContentConverter by extending(isFallback = true) {
        commentsToContentConverter with DocTagToContentConverter
    }

    val pageMerger by extending {
        CoreExtensions.pageTransformer providing { ctx -> PageMerger(ctx[pageMergerStrategy]) }
    }

    val fallbackMerger by extending {
        pageMergerStrategy providing { ctx -> FallbackPageMergerStrategy(ctx.logger) }
    }

    val sameMethodNameMerger by extending {
        pageMergerStrategy with SameMethodNamePageMergerStrategy order {
            before(fallbackMerger)
        }
    }

    val htmlRenderer by extending {
        CoreExtensions.renderer providing ::HtmlRenderer applyIf { format == "html" }
    }

    val locationProvider by extending(isFallback = true) {
        locationProviderFactory providing ::DefaultLocationProviderFactory
    }

    val fileWriter by extending(isFallback = true) {
        outputWriter providing ::FileWriter
    }

    val rootCreator by extending {
        htmlPreprocessors with RootCreator order { before(navigationPageInstaller) }
    }

    val navigationPageInstaller by extending {
        htmlPreprocessors with NavigationPageInstaller order { before(searchPageInstaller) }
    }

    val searchPageInstaller by extending {
        htmlPreprocessors with SearchPageInstaller order { before(resourceInstaller) }
    }

    val resourceInstaller by extending {
        htmlPreprocessors with ResourceInstaller order { before(styleAndScriptsAppender) }
    }

    val styleAndScriptsAppender by extending {
        htmlPreprocessors with StyleAndScriptsAppender
    }


    val defaultPreprocessors =
        listOf(RootCreator, SearchPageInstaller, NavigationPageInstaller, ResourceInstaller, StyleAndScriptsAppender)
}