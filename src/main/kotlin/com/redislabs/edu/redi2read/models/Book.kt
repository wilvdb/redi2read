package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash

@RedisHash
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
            property = "id")
data class Book(
    @Id
    var id: String?,
    var title: String?,
    var subtitle: String?,
    var description: String?,
    var language: String?,
    var pageCount: Long?,
    var thumbnail: String?,
    var price: Double?,
    var currency: String?,
    var infoLink: String?,
    var  authors: Set<String>?,
    @Reference
    var categories: HashSet<Category> = HashSet(),
) {

    constructor(): this(null, "", null, null, "", 0L, "", 0.0, null, "", null, HashSet())

    fun addCategory(category: Category) {
        categories.add(category);
    }
}