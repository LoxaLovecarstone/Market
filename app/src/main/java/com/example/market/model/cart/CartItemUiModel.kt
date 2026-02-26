package com.example.market.model.cart

import com.example.market.model.common.Product

data class CartItemUiModel(
    val product: Product,
    val quantity: Int
)