package com.redislabs.edu.redi2read.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration

@Configuration
class Config {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val template = RedisTemplate<Any, Any>()
        template.setConnectionFactory(connectionFactory)
        return template
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .prefixCacheNameWith("${this.javaClass.packageName }.")
            .entryTtl(Duration.ofHours(1))
            .disableCachingNullValues()

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build()
    }

}