package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class Cart(
    var id: String?,
    var userId: String,
    var cartItems: Set<CartItem>,
) {
    fun count() = cartItems.size

    fun getTotal() = cartItems
        .stream()
        .mapToDouble { it.price?:0.0 * it.quantity }
        .sum()
}

@RedisHash
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
            property = "id")
class Book(
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

@RedisHash
class BookRating(
    @Id
    var id: String?,

    @NotNull
    @Reference
    var user: User?,

    @NotNull
    @Reference
    var book: Book?,

    @NotNull
    var rating: Int?,
) {

    constructor(): this(null, null, null, null)

}

class CartItem(
    var isbn: String,
    var price: Double?,
    var quantity: Long,
) {
}

@RedisHash
class Category(
    @Id
    var id:String?,
    var name: String?,
) {
}

@RedisHash
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id")
class Role(@Id var id: String?, @Indexed var name: String?) {
}

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