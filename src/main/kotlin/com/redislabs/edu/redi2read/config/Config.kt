package com.redislabs.edu.redi2read.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class Config {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val template = RedisTemplate<Any, Any>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}