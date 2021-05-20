package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.lettusearch.CreateOptions
import com.redislabs.lettusearch.Field
import com.redislabs.lettusearch.StatefulRediSearchConnection
import io.lettuce.core.RedisCommandExecutionException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.reflect.jvm.jvmName

@Component
@Order(6)
class CreateBooksSearchIndex(
    @Autowired val searchConnection: StatefulRediSearchConnection<String, String>,
    @Value("\${app.booksSearchIndexName}") val searchIndexName: String,
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        val commands = searchConnection.sync()
        try {
            commands.ftInfo(searchIndexName);
        } catch (rcee: RedisCommandExecutionException) {
            if (rcee.message == "Unknown Index name") {
                val options: CreateOptions<String, String> = CreateOptions.builder<String, String>().prefix(String.format("%s:", Book::class.jvmName)).build()
                val title = Field.text("title").sortable(true).build()
                val subtitle = Field.text("subtitle").build()
                val description = Field.text("description").build()
                val author0 = Field.text("authors.[0]").build()
                val author1 = Field.text("authors.[1]").build()
                val author2 = Field.text("authors.[2]").build()
                val author3 = Field.text("authors.[3]").build()
                val author4 = Field.text("authors.[4]").build()
                val author5 = Field.text("authors.[5]").build()
                val author6 = Field.text("authors.[6]").build()
                commands.create(
                    searchIndexName, //
                    options, //
                    title, subtitle, description, //
                    author0, author1, author2, author3, author4, author5, author6 //
                )
                logger.info(">>>> Created Books Search Index...")
            }
        }
    }
}