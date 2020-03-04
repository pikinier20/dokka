package org.jetbrains.dokka.model

import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.properties.ExtraProperty

class AdditionalModifiers(val content: List<ExtraModifiers>) : ExtraProperty<Documentable> {
    object AdditionalKey : ExtraProperty.Key<Documentable, AdditionalModifiers>

    override fun equals(other: Any?): Boolean = if (other is AdditionalModifiers) other.content == content else false
    override fun hashCode() = content.hashCode()
    override val key: ExtraProperty.Key<Documentable, *> = AdditionalKey
}

class Annotations(val content: List<Annotation>): ExtraProperty<Documentable> {
    object AnnotationsKey : ExtraProperty.Key<Documentable, Annotations>

    override val key: ExtraProperty.Key<Documentable, *> = AnnotationsKey

    data class Annotation(val dri: DRI, val params: Map<String, String>)
}