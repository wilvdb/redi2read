package com.redislabs.edu.redi2read.models

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import javax.validation.constraints.NotNull

@RedisHash
data class BookRating(
    @Id
    var id: String?,

    @NotNull
    @Reference
    var user: User?,

    @NotNull
    @Reference
    var book: Book?,

    @NotNull
    var rating: Int?,
) {

    constructor(): this(null, null, null, null)

}