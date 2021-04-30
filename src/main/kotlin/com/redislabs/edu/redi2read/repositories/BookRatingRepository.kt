package com.redislabs.edu.redi2read.repositories

import com.redislabs.edu.redi2read.models.BookRating
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRatingRepository: CrudRepository<BookRating, String> {
}