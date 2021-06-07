package com.redislabs.edu.redi2read.services

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.CartItem
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CartRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import com.redislabs.modules.rejson.JReJSON
import com.redislabs.modules.rejson.Path
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartService(
    @Autowired val cartRepository: CartRepository,
    @Autowired val bookRepository: BookRepository,
    @Autowired val userRepository: UserRepository,
) {

    private val redisJson = JReJSON()
    private val cartItemsPath: Path = Path.of(".cartItems")

    fun get(id: String) = cartRepository.findById(id).get()

    fun addToCart(id: String?, item: CartItem) {
        val book = bookRepository.findByIdOrNull(item.isbn)
        book?.let {
            val cartKey: String = CartRepository.getKey(id)
            item.price = it.price
            redisJson.arrAppend(cartKey, cartItemsPath, item)
        }
    }

    fun removeFromCart(id: String, isbn: String) {
        val cart = cartRepository.findByIdOrNull(id)
        cart?.let { it ->
            val cartKey = CartRepository.getKey(it.id)
            val cartItems = ArrayList(cart.cartItems)
            val cartItemIndex = (0..cartItems.size.toLong()).firstOrNull { index -> cartItems[index.toInt()].isbn == isbn }
            cartItemIndex?.let { redisJson.arrPop(cartKey, CartItem::class.java, cartItemsPath, cartItemIndex) }
        }
    }

    fun checkout(id: String) {
        val cart = cartRepository.findByIdOrNull(id)
        cart?.let {
            val user = userRepository.findByIdOrNull(it.userId)
            user?.let {
                cart.cartItems.forEach {
                    val book: Book = bookRepository.findById(it.isbn).get()
                    user.addBook(book)
                }
                userRepository.save<User>(user)
            }
        }
        // cartRepository.delete(cart);
    }
}