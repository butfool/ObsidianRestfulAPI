package cool.but.obsidian.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

object Singleton {
    val yamlObjectMapper = ObjectMapper(YAMLFactory()).apply {
        findAndRegisterModules()
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    val objectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }
}