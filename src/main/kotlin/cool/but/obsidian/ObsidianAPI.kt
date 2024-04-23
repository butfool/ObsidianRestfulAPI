package cool.but.obsidian

import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.obsidian.api.ObsidianDocumentClient
import cool.but.obsidian.config.ObsidianConfiguration
import cool.but.obsidian.entity.MarkdownFile
import org.springframework.stereotype.Service

@Service
class ObsidianAPI(
    private val obsidianDocumentClient: ObsidianDocumentClient,
    private val obsidianConfiguration: ObsidianConfiguration,
) {

    fun upsertDocument(markdownFile: MarkdownFile): ForestResponse<JsonNode> {
        val name = if (markdownFile.name!!.endsWith(".md")) {
            markdownFile.name!!
        } else {
            markdownFile.name!! + ".md"
        }
        return obsidianDocumentClient.createDocument(
            filename = "${markdownFile.path!!}/$name",
            content = markdownFile.toString(),
            headers = obsidianConfiguration.headersMap
        )
    }
}