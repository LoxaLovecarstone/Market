package com.example.market.data.repository

import com.example.market.data.local.CartDao
import com.example.market.model.cart.CartItem
import com.example.market.model.cart.CartItemUiModel
import com.example.market.repository.CartRepository
import com.example.market.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val homeRepository: HomeRepository
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItemUiModel>> {
        return cartDao.getAllCartItems().map { cartEntities ->
            // 실무에서는 전체 상품 정보를 캐싱하거나 특정 ID들만 조회하는 API를 쓰지만,
            // 현재는 가짜 데이터를 활용하여 결합합니다.
            val homeData = homeRepository.getHomeData().getOrNull()
            val allProducts = homeData?.products ?: emptyList()

            cartEntities.mapNotNull { entity ->
                val product = allProducts.find { it.id == entity.productId }
                product?.let { CartItemUiModel(it, entity.quantity) }
            }
        }
    }

    override suspend fun addToCart(productId: String) {
        cartDao.insertCartItem(CartItem(productId = productId))
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        cartDao.insertCartItem(CartItem(productId = productId, quantity = quantity))
    }

    override suspend fun deleteCartItem(productId: String) {
        cartDao.deleteCartItem(CartItem(productId = productId))
    }
}