package com.example.market.model.common

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val categoryId: String
)