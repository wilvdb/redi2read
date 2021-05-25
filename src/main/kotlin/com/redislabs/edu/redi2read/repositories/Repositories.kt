package com.redislabs.edu.redi2read.repositories

import com.redislabs.edu.redi2read.models.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: PagingAndSortingRepository<Book, String> {
}

@Repository
interface BookRatingRepository: CrudRepository<BookRating, String> {
}

@Repository
interface CategoryRepository: CrudRepository<Category, String> {
}

@Repository
interface RoleRepository: CrudRepository<Role, String> {

    fun findFirstByName(role: String): Role
}

@Repository
interface UserRepository: CrudRepository<User, String> {

    fun findFirstByEmail(email: String): User?
}