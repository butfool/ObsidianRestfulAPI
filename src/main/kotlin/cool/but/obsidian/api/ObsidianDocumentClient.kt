package cool.but.obsidian.api

import com.dtflys.forest.annotation.*
import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.obsidian.config.ObsidianServerAddress
import cool.but.obsidian.data.dto.SimpleSearchResult
import org.springframework.stereotype.Component

@Component
interface ObsidianDocumentClient {

    @Put("/vault/{filename}")
    @Address(source = ObsidianServerAddress::class)
    fun createDocument(
        @Var("filename") filename: String,
        @Body content: String,
        @Header headers: Map<String, String>,
    ): ForestResponse<JsonNode>


    @Post("/search/simple")
    @Address(source = ObsidianServerAddress::class)
    fun searchDocumentByText(
        @Query("query") query: String,
        @Query("contextLength") contextLength: Int = 0,
    ): List<SimpleSearchResult>

}


