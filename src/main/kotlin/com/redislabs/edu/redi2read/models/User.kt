package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@JsonIgnoreProperties(value = ["password", "passwordConfirm"], allowSetters = true)
@RedisHash
class User(
    @Id
    var id: String?,

    @NotNull
    @Size(min = 2, max = 48)
    var name: String,

    @NotNull
    @Email
    @Indexed
    var email: String,

    @NotNull
    var password: String,

    @Transient
    var passwordConfirm: String?,

    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    var roles: HashSet<Role> = HashSet(),

    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    var books: HashSet<Book> = HashSet()
) {

    constructor(): this(null, "", "", "", "")

    fun addRole(role: Role) {
        roles.add(role)
    }

    fun addBook(book: Book) {
        books.add(book)
    }
}