package com.redislabs.edu.redi2read.models

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash
data class Role(@Id var id: String?, @Indexed var name: String?) {
}