package com.redislabs.edu.redi2read.models

data class CartItem(
    var isbn: String,
    var price: Double?,
    var quantity: Long,
) {
}