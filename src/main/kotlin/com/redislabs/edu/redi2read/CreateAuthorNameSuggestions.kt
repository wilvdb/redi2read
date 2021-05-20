package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.lettusearch.StatefulRediSearchConnection
import com.redislabs.lettusearch.Suggestion
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
@Order(7)
class CreateAuthorNameSuggestions(
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val bookRepository: BookRepository,
    @Autowired val  searchConnection: StatefulRediSearchConnection<String, String>,
    @Value("\${app.autoCompleteKey}") val autoCompleteKey: String,
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (!redisTemplate.hasKey(autoCompleteKey)) {
            val commands = searchConnection.sync()
            bookRepository.findAll().forEach {book ->
                book.authors?.forEach{ author ->
                    run {
                        val suggestion = Suggestion.builder(author).score(1.0).build()
                        commands.sugadd(autoCompleteKey, suggestion)
                    }
                }
            }
            logger.info(">>>> Created Author Name Suggestions...");
        }
    }
}