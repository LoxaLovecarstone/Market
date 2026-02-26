package com.example.market.repository

import com.example.market.model.cart.CartItemUiModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItemUiModel>>
    suspend fun addToCart(productId: String)
    suspend fun updateQuantity(productId: String, quantity: Int)
    suspend fun deleteCartItem(productId: String)
}