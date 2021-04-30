package com.redislabs.edu.redi2read.repositories

import com.redislabs.edu.redi2read.models.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<User, String> {

    fun findFirstByEmail(email: String): User?
}