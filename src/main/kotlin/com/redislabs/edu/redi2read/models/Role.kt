package com.redislabs.edu.redi2read.models

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash
data class Role(@Id val id: String?, val name: String) {
}