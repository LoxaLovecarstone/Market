package com.example.market.ui.mypage

import com.example.market.model.common.Product

data class MyPageUiState(
    val name: String = "김동찬",
    val email: String = "kdc6208@naver.com",
    val profileImageUrl: String = "https://picsum.photos/id/64/200",
    val shippingProducts: List<Product> = emptyList(),
    val deliveredProducts: List<Product> = emptyList()
)