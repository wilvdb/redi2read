package com.redislabs.edu.redi2read.models

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