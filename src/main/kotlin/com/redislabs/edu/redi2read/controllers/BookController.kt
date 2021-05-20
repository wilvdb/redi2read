package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import com.redislabs.lettusearch.SearchResults
import com.redislabs.lettusearch.StatefulRediSearchConnection
import com.redislabs.lettusearch.Suggestion
import com.redislabs.lettusearch.SuggetOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/books")
class BookController(
    @Autowired val bookRepository: BookRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Value("\${app.booksSearchIndexName}") val searchIndexName: String,
    @Autowired val searchConnection: StatefulRediSearchConnection<String, String>,
    @Value("\${app.autoCompleteKey}") val autoCompleteKey: String,
) {

    @GetMapping
    fun all(@RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int): ResponseEntity<Any> {
        val paging = PageRequest.of(page, size)
        val pagedResult = bookRepository.findAll(paging)
        val books = if (pagedResult.hasContent()) pagedResult.content else emptyList<Book>()
        val response: MutableMap<String, Any> = HashMap()
        response["books"] = books
        response["page"] = pagedResult.number
        response["pages"] = pagedResult.totalPages
        response["total"] = pagedResult.totalElements
        return ResponseEntity<Any>(response, HttpHeaders(), HttpStatus.OK)
    }

    @GetMapping("/categories")
    fun getCategories() =  categoryRepository.findAll()

    @GetMapping("/{isbn}")
    fun get(@PathVariable("isbn") isbn: String) = bookRepository.findById(isbn).get()

    @GetMapping("/search")
    fun search(@RequestParam(name="q") query: String): SearchResults<String, String> {
        val commands = searchConnection.sync()
        return commands.search(searchIndexName, query)
    }

    @GetMapping("/authors")
    fun authorAutoComplete(@RequestParam(name="q") query: String): List<Suggestion<String>> {
        val commands = searchConnection.sync()
        val options = SuggetOptions.builder().max(20L).build()
        return commands.sugget(autoCompleteKey, query, options)
    }

}