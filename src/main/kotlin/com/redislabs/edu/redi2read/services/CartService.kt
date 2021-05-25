package com.redislabs.edu.redi2read.services

import com.redislabs.edu.redi2read.models.Book
import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.edu.redi2read.models.CartItem
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CartRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import com.redislabs.modules.rejson.JReJSON
import com.redislabs.modules.rejson.Path
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartService(
    @Autowired val cartRepository: CartRepository,
    @Autowired val bookRepository: BookRepository,
    @Autowired val userRepository: UserRepository,
) {

    private val redisJson = JReJSON()
    val cartItemsPath: Path = Path.of(".cartItems")

    fun get(id: String) = cartRepository.findById(id).get()

    fun addToCart(id: String?, item: CartItem) {
        val book: Optional<Book> = bookRepository.findById(item.isbn)
        if (book.isPresent) {
            val cartKey: String = CartRepository.getKey(id)
            item.price = book.get().price
            redisJson.arrAppend(cartKey, cartItemsPath, item)
        }
    }

    fun removeFromCart(id: String, isbn: String) {
        val cartFinder: Optional<Cart> = cartRepository.findById(id)
        if (cartFinder.isPresent) {
            val cart: Cart = cartFinder.get()
            val cartKey: String = CartRepository.getKey(cart.id)
            val cartItems: List<CartItem> = ArrayList(cart.cartItems)
            val cartItemIndex = (0..cartItems.size.toLong()).firstOrNull { cartItems[it.toInt()].isbn == isbn }
            cartItemIndex?.let { redisJson.arrPop(cartKey, CartItem::class.java, cartItemsPath, cartItemIndex) }
        }
    }

    fun checkout(id: String) {
        val cart = cartRepository.findById(id).get()
        val user: User = userRepository.findById(cart.userId).get()
        cart.cartItems.forEach {
            val book: Book = bookRepository.findById(it.isbn).get()
            user.addBook(book)
        }
        userRepository.save<User>(user)
        // cartRepository.delete(cart);
    }
}