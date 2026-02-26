package com.example.market.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.market.ui.cart.CartScreen
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen
import com.example.market.ui.common.UiState
import com.example.market.ui.home.HomeScreen
import com.example.market.ui.home.detail.ProductDetailScreen
import com.example.market.ui.navigation.BottomNavItem
import com.example.market.ui.navigation.Route
import kotlinx.coroutines.delay

@Composable
fun MainScreen() {  // MainScreen은 "어디로 갈지"만 정합니다.
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 바텀바를 보여줄 라우트 리스트
    val bottomBarRoutes = listOf(
        Route.Home.route,
        Route.Category.route,
        Route.MyPage.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar {
                    val items = listOf(
                        BottomNavItem.HOME,
                        BottomNavItem.CATEGORY,
                        BottomNavItem.MY_PAGE
                    )
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    // 네비게이션 스택 관리 (중복 생성 방지 등)
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                // 뷰모델이고 상태고 뭐고 여기선 HomeScreen 하나만 호출하면 끝입니다.
                HomeScreen(
                    onProductClick = { productId ->
                        navController.navigate(Route.ProductDetail.createRoute(productId))
                    },
                    onCartClick = {
                        navController.navigate(Route.Cart.route)
                    }
                )
            }

            composable(
                route = Route.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) {
                // 상세 화면 컴포저블 호출 (아직 안 만들었으니 임시 텍스트나 빈 화면)
                ProductDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Route.Category.route) {
                Text("카테고리 화면")
            }

            composable(Route.MyPage.route) {
                Text("마이페이지 화면")
            }

            composable(Route.Cart.route) { // 테스트를 위해 카테고리 자리에 장바구니를 연결
                CartScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}