package com.redislabs.edu.redi2read.models

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash
class Category(
    @Id
    var id:String?,
    var name: String?,
) {
}