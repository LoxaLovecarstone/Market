package com.example.market.data.repository

import com.example.market.model.category.Category
import com.example.market.model.common.Product
import com.example.market.model.home.Banner
import com.example.market.model.home.HomeData
import com.example.market.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    private val mockProducts = listOf(
        Product("1", "유기농 사과", 15000, "https://picsum.photos/id/10/400", "fruit"),
        Product("2", "신선한 우유", 3500, "https://picsum.photos/id/20/400", "dairy"),
        Product("3", "통밀 식빵", 4800, "https://picsum.photos/id/30/400", "bakery"),
        Product("4", "맛있는 닭가슴살", 12000, "https://picsum.photos/id/40/400", "meat"),
        Product("5", "꿀 참외", 8000, "https://picsum.photos/id/11/400", "fruit"),
        Product("6", "플레인 요거트", 4200, "https://picsum.photos/id/21/400", "dairy"),
        Product("7", "버터 크루아상", 3500, "https://picsum.photos/id/31/400", "bakery"),
        Product("8", "제주 흑돼지", 38000, "https://picsum.photos/id/41/400", "meat")
    )

    private val mockCategories = listOf(
        Category("fruit", "과일", "https://picsum.photos/id/10/200"),
        Category("dairy", "유제품", "https://picsum.photos/id/20/200"),
        Category("bakery", "베이커리", "https://picsum.photos/id/30/200"),
        Category("meat", "정육", "https://picsum.photos/id/40/200")
    )

    override suspend fun getHomeData(): Result<HomeData> = runCatching {
        HomeData(
            banners = listOf(
                Banner("1", "https://picsum.photos/id/10/400/200", "신상품 출시 이벤트"),
                Banner("2", "https://picsum.photos/id/20/400/200", "주말 한정 특가 세일")
            ),
            products = mockProducts
        )
    }

    override suspend fun getProductById(productId: String): Result<Product> = runCatching {
        mockProducts.find { it.id == productId } ?: throw NoSuchElementException("상품 없음")
    }

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        mockCategories
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = runCatching {
        mockProducts.filter { it.categoryId == categoryId }
    }
}