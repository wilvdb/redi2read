package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.edu.redi2read.models.CartItem
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CartRepository
import com.redislabs.edu.redi2read.services.CartService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.HashSet
import kotlin.reflect.jvm.jvmName

@Component
@Order(5)
class CreateCards(
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val cartRepository: CartRepository,
    @Autowired val bookRepository: BookRepository,
    @Autowired val cartService: CartService,
    @Value("\${app.numberOfCarts}") val numberOfCarts: Int,
) : CommandLineRunner {

    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
    }

    override fun run(vararg args: String?) {
        if (cartRepository.count() == 0L) {
            val random = Random()
            // loops for the number of carts to create
            IntStream.range(0, numberOfCarts).forEach {
                // get a random user
                val userId: String = redisTemplate.opsForSet().randomMember(User::class.jvmName)
                // make a cart for the user
                // get between 1 and 7 books
                val books = getRandomBooks(bookRepository, 7)
                val cart = Cart(null, userId, getCartItemsForBooks(books))
                // save the cart
                cartRepository.save(cart)
                // randomly checkout carts
                if (random.nextBoolean()) {
                    cartService.checkout(cart.id!!)
                }
            }
            logger.info(">>>> Created Carts...");
        }
    }

    private fun getRandomBooks(bookRepository: BookRepository, max: Int): Set<Book> {
        val random = Random()
        val howMany = random.nextInt(max) + 1
        val books = HashSet<Book>()
        (1..howMany).forEach { _ ->
            val randomBookId = redisTemplate.opsForSet().randomMember(Book::class.jvmName)
            books.add(bookRepository.findById(randomBookId).get())
        }
        return books;
    }

    private fun  getCartItemsForBooks(books: Set<Book>): Set<CartItem> {
        val items =  HashSet<CartItem>()
        books.forEach { items.add(CartItem(it.id!!, it.price, 1L)) }
        return items;
    }
}