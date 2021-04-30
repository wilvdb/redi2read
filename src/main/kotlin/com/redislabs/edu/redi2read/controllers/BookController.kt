package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus

import org.springframework.http.ResponseEntity

import java.util.HashMap

import java.util.Collections





@RestController
@RequestMapping("/api/books")
class BookController(
    @Autowired val bookRepository: BookRepository,
    @Autowired val categoryRepository: CategoryRepository,
) {

    @GetMapping
    fun all(@RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int): ResponseEntity<Any> {
        val paging = PageRequest.of(page, size)
        val pagedResult = bookRepository.findAll(paging)
        val books = if (pagedResult.hasContent()) pagedResult.content else emptyList<Book>()
        val response: MutableMap<String, Any> = HashMap()
        response["books"] = books
        response["page"] = pagedResult.getNumber()
        response["pages"] = pagedResult.getTotalPages()
        response["total"] = pagedResult.getTotalElements()
        return ResponseEntity<Any>(response, HttpHeaders(), HttpStatus.OK)
    }

    @GetMapping("/categories")
    fun getCategories() =  categoryRepository.findAll()

    @GetMapping("/{isbn}")
    fun get(@PathVariable("isbn") isbn: String) = bookRepository.findById(isbn).get()

}