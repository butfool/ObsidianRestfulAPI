package cool.but.obsidian.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

object Singleton {
    val yamlObjectMapper = ObjectMapper(YAMLFactory()).apply {
        findAndRegisterModules()
    }
    val objectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }
}