package com.example.market.repository

import com.example.market.model.home.HomeData

interface HomeRepository {
    suspend fun getHomeData(): Result<HomeData>
}