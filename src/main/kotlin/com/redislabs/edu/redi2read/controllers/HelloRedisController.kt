package com.redislabs.edu.redi2read.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.AbstractMap.SimpleEntry

import org.springframework.web.server.ResponseStatusException

import org.springframework.web.bind.annotation.PathVariable

import org.springframework.web.bind.annotation.GetMapping





@RestController
@RequestMapping("/api/redis")
class HelloRedisController(@Autowired val template: RedisTemplate<String, String>) {

    private val STRING_KEY_PREFIX = "redi2read:strings:"

    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    fun setString(@RequestBody kvp: Map.Entry<String, String>): Map.Entry<String, String> {
        template.opsForValue().set(STRING_KEY_PREFIX + kvp.key, kvp.value)
        return kvp
    }

    @GetMapping("/strings/{key}")
    fun getString(@PathVariable("key") key: String): Map.Entry<String, String> {
        val value = template.opsForValue()[STRING_KEY_PREFIX + key]
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "key not found")
        return SimpleEntry(key, value)
    }
}