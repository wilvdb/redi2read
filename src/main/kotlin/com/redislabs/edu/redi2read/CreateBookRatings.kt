package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.BookRating
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.BookRatingRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.jvm.jvmName

@Component
@Order(4)
class CreateBookRatings(
    @Value("\${app.numberOfRatings}") val numberOfRatings: Int,
    @Value("\${app.ratingStars}") val ratingStars: Int,
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val bookRatingRepo: BookRatingRepository,
): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (bookRatingRepo.count() == 0L) {
            val random = Random()
            (0..numberOfRatings).forEach { _ ->
                val bookId = redisTemplate.opsForSet().randomMember(Book::class.jvmName)
                val userId = redisTemplate.opsForSet().randomMember(User::class.jvmName)
                val stars = random.nextInt(ratingStars) + 1
                val user = User()
                user.id = userId
                val book = Book()
                book.id = bookId
                val rating = BookRating().apply {
                    this.user = user
                    this.book = book
                    this.rating = stars
                }
                bookRatingRepo.save(rating)
            }
            logger.info(">>>> BookRating created...")
        }
    }
}