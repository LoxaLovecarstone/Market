package com.example.market.repository

import com.example.market.model.category.Category
import com.example.market.model.common.Product
import com.example.market.model.home.HomeData

interface HomeRepository {
    suspend fun getHomeData(): Result<HomeData>
    suspend fun getProductById(productId: String): Result<Product>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>>

    suspend fun searchProducts(query: String, categoryId: String? = null): Result<List<Product>>

}