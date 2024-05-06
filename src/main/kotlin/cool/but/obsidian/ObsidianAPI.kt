package cool.but.obsidian

import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.kt.common.utils.AssertUtils
import cool.but.obsidian.api.ObsidianDocumentClient
import cool.but.obsidian.config.ObsidianConfiguration
import cool.but.obsidian.data.dto.ListDocumentByPathResult
import cool.but.obsidian.data.dto.SimpleSearchResult
import cool.but.obsidian.entity.MarkdownFile
import cool.but.obsidian.entity.MarkdownProperties
import cool.but.obsidian.exception.ObsidianException
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

    fun getDocument(path: String): Pair<Boolean, String> {
        val realPath = if (!path.endsWith(".md")) {
            "$path.md"
        } else {
            path
        }
        val response = obsidianDocumentClient.getDocument(
            filename = realPath,
            headers = obsidianConfiguration.headersMap
        )
        return (response.statusCode == 200) to response.content
    }

    fun getDocument(path: String, propertiesClass: Class<out MarkdownProperties>): Pair<Boolean, MarkdownFile> {
        val ret = getDocument(path)
        return ret.first to MarkdownFile.fromString(ret.second, propertiesClass).apply {
            // 文件路径
            val lastIndex = path.lastIndexOf('/')
            this.path = path.substring(0, lastIndex)
            this.name = path.substring(lastIndex + 1, path.length)
        }
    }

    fun getDocumentByFileName(filename: String, propertiesClass: Class<out MarkdownProperties>): Pair<Boolean, MarkdownFile> {
        AssertUtils.throwIf(filename.length < 3, ObsidianException("文件名长度小于 3：$filename"))
        val searchResult = searchDocumentByFileName(filename)
        AssertUtils.throwIf(searchResult.size != 1, ObsidianException("文件数量不为 1：${searchResult.size}"))
        return getDocument(searchResult[0].filename!!, propertiesClass)
    }


    fun searchDocumentByFileName(filename: String): List<SimpleSearchResult> {
        return obsidianDocumentClient.searchDocumentByText(filename, headers = obsidianConfiguration.headersMap)
    }

    fun listDocumentByPath(path: String): ListDocumentByPathResult {
        if (path.isBlank()) {
            return ListDocumentByPathResult().apply {
                files = emptyList()
            }
        }
        val realPath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }
        return obsidianDocumentClient.listDocumentsByPath(realPath, obsidianConfiguration.headersMap)
    }
}