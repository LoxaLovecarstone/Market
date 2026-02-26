package com.example.market.data.repository

import com.example.market.model.common.Product
import com.example.market.model.home.Banner
import com.example.market.model.home.HomeData
import com.example.market.repository.HomeRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    private val mockProducts = listOf(
        Product("1", "유기농 사과", 15000, "https://picsum.photos/id/10/400", "fruit"),
        Product("2", "신선한 우유", 3500, "https://picsum.photos/id/20/400", "dairy"),
        Product("3", "통밀 식빵", 4800, "https://picsum.photos/id/30/400", "bakery"),
        Product("4", "맛있는 닭가슴살", 12000, "https://picsum.photos/id/40/400", "meat")
    )

    override suspend fun getHomeData(): Result<HomeData> = runCatching {
        delay(1500)

        HomeData(
            banners = listOf(
                // 각기 다른 id를 부여하여 이미지를 구분합니다.
                Banner("1", "https://picsum.photos/id/10/400/200", "신상품 출시 이벤트"),
                Banner("2", "https://picsum.photos/id/20/400/200", "주말 한정 특가 세일"),
                Banner("3", "https://picsum.photos/id/30/400/200", "신규 가입 혜택")
            ),
            products = mockProducts
        )
    }

    override suspend fun getProductById(productId: String): Result<Product> = runCatching {
        delay(500)
        val products = mockProducts
        products.find { it.id == productId } ?: throw NoSuchElementException("상품 없음")}
}