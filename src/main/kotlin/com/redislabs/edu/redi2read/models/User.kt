package com.redislabs.edu.redi2read.models

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RedisHash
class User(
    @Id
    val id: String,

    @NotNull
    @Size(min = 2, max = 48)
    val name: String,

    @NotNull
    @Email
    @Indexed
    val email: String,

    @NotNull
    val password: String,

    @Transient
    val passwordConfirm: String,

    @Reference
    val roles: HashSet<Role> = HashSet<Role>()
) {

    fun addRole(role: Role) {
        roles.add(role)
    }
}