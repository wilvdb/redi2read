package com.redislabs.edu.redi2read

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.Category
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

@Component
@Order(3)
class CreateBooks(
    @Autowired val bookRepository: BookRepository,
    @Autowired val categoryRepository: CategoryRepository,
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (bookRepository.count() == 0L) {
            val mapper = ObjectMapper()
            val files = //
                Files.list(Paths.get(javaClass.getResource("/data/books").toURI())) //
                    .filter(Files::isRegularFile) //
                    .filter { it.toString().endsWith(".json") }
                    .map(java.nio.file.Path::toFile) //
                    .collect(Collectors.toList())
            val categories = HashMap<String, Category>()
            files.forEach {
                try {
                    logger.info(">>>> Processing Book File: ${it.path}")
                    val categoryName = it.name.substring(0, it.name.lastIndexOf("_"))
                    logger.info(">>>> Category: $categoryName")
                    val category: Category
                    if (!categories.containsKey(categoryName)) {
                        category = Category(null, categoryName)
                        categoryRepository.save(category)
                        categories[categoryName] = category
                    } else {
                        category = categories[categoryName]!!
                    }
                    val inputStream = FileInputStream(it)
                    val books: List<Book> = mapper.readValue(inputStream)
                    books.stream().forEach { book ->
                        book.addCategory(category)
                        bookRepository.save(book)
                    }
                    logger.info(">>>> ${books.size} Books Saved!")
                } catch (e: IOException) {
                    logger.info("Unable to import books: ${e.message}")
                }
            }
            logger.info(">>>> Loaded Book Data and Created books...")
        }
    }
}