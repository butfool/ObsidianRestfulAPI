package cool.but.obsidian.entity

import cool.but.iwhale.data.obsidian.MarkdownProperties
import cool.but.obsidian.utils.Singleton

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
}