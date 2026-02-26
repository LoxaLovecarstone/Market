package com.example.market.repository

import com.example.market.model.common.Product
import com.example.market.model.home.HomeData

interface HomeRepository {
    suspend fun getHomeData(): Result<HomeData>
    suspend fun getProductById(productId: String): Result<Product>
}