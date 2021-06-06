package com.redislabs.edu.redi2read

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class Redi2readApplication

fun main(args: Array<String>) {
    runApplication<Redi2readApplication>(*args)
}

