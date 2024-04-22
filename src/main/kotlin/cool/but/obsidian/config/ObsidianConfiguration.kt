package cool.but.obsidian.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration

@SpringBootConfiguration
class ObsidianConfiguration {

    @Value("obsidian.host")
    var host: String = "localhost"

    @Value("obsidian.port")
    var port: Int = 27124

    @Value("obsidian.token")
    var token: String? = null

    val headersMap: Map<String, String> by lazy {
        mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "text/markdown",
        )
    }

}