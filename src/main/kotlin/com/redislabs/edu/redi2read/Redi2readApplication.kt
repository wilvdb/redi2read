package com.redislabs.edu.redi2read

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@SpringBootApplication
class Redi2readApplication

fun main(args: Array<String>) {
    runApplication<Redi2readApplication>(*args)
}

@Bean
fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
    val template = RedisTemplate<Any, Any>();
    template.setConnectionFactory(connectionFactory);
    return template;
}