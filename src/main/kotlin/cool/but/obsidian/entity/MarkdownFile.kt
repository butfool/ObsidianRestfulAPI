package cool.but.obsidian.entity

import cool.but.obsidian.utils.Singleton
import kotlin.reflect.KClass

data class MarkdownFile(
    var path: String? = null,
    var name: String? = null,
    var properties: MarkdownProperties? = null,
    var content: String? = null,
) {
    override fun toString(): String {
        return StringBuilder().apply {
            if (properties != null) {
                append(Singleton.yamlObjectMapper.writeValueAsString(properties))
                append("---\n\n")
            }
            append(content)
        }.toString()
    }

    companion object {
        private val EMPTY_INSTANCE = MarkdownFile()

        fun fromString(content: String, propertiesClass: Class<out MarkdownProperties>): MarkdownFile {
            if (content.isBlank()) {
                return EMPTY_INSTANCE
            }
            val properties = if (content.startsWith("---")) {
                val end = content.indexOf("\n---\n")
                if (end == -1) {
                    null
                } else {
                    val yaml = content.substring(4, end)
                    Singleton.yamlObjectMapper.readValue(yaml, propertiesClass)
                }
            } else {
                null
            }
            val realContent = if (properties != null) {
                content.substring(content.indexOf("\n---\n") + 5)
            } else {
                content
            }
            return MarkdownFile(properties = properties, content = realContent)
        }
    }
}