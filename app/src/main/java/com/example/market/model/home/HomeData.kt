package com.example.market.model.home

import com.example.market.model.common.Product

data class HomeData(
    val banners: List<Banner>,
    val products: List<Product>
)