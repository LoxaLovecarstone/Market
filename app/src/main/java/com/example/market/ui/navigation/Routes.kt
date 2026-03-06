package com.example.market.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(val route: String) {
    data object Home: Route("home")
    data object Category: Route("category")
    data object MyPage: Route("mypage")

    data object OrderList : Route("order_list/{type}") {
        fun createRoute(type: String) = "order_list/$type"
    }

    data object ProductDetail : Route("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }

    data object Cart : Route("cart")
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME(Route.Home.route, "홈", Icons.Default.Home),
    CATEGORY(Route.Category.route, "카테고리", Icons.Default.Search),
    MY_PAGE(Route.MyPage.route, "내 정보", Icons.Default.Person)
}