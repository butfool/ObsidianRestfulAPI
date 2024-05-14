package cool.but.obsidian.api

import com.dtflys.forest.annotation.*
import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.obsidian.config.ObsidianServerAddress
import cool.but.obsidian.data.dto.ListDocumentByPathResult
import cool.but.obsidian.data.dto.SimpleSearchResult
import org.springframework.stereotype.Component

@Component
interface ObsidianDocumentClient {


    @Delete("/vault/{filename}")
    @Address(source = ObsidianServerAddress::class)
    fun deleteDocument(
        @Var("filename") path: String,
        @Header headers: Map<String, String>,
    ): ForestResponse<JsonNode>

    @Put("/vault/{filename}")
    @Address(source = ObsidianServerAddress::class)
    fun createDocument(
        @Var("filename") path: String,
        @Body content: String,
        @Header headers: Map<String, String>,
    ): ForestResponse<JsonNode>


    @Get("/vault/{filename}")
    @Address(source = ObsidianServerAddress::class)
    fun getDocument(
        @Var("filename") path: String,
        @Header headers: Map<String, String>,
    ): ForestResponse<String>


    @Post("/search/simple")
    @Address(source = ObsidianServerAddress::class)
    fun searchDocumentByText(
        @Query("query") query: String,
        @Query("contextLength") contextLength: Int = 0,
        @Header headers: Map<String, String>,
    ): List<SimpleSearchResult>

    @Get("/vault/{path}")
    @Address(source = ObsidianServerAddress::class)
    fun listDocumentsByPath(
        @Var("path") path: String,
        @Header headers: Map<String, String>,
        ): ListDocumentByPathResult

}


