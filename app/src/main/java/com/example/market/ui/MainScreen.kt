package com.example.market.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen
import com.example.market.ui.common.UiState
import com.example.market.ui.navigation.BottomNavItem
import com.example.market.ui.navigation.Route
import kotlinx.coroutines.delay

@Composable
fun MainScreen() {  // MainScreen은 "어디로 갈지"만 정합니다.
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                // 1. 가짜 상태 관리: 초기값은 Loading
                var uiState by remember { mutableStateOf<UiState<String>>(UiState.Loading) }

                // 2. 2초 후 Success로 변경하는 비동기 로직
                LaunchedEffect(Unit) {
                    delay(2000)
                    uiState = UiState.Success("마켓 데이터 로드 성공")
                }

                // 3. 공통 화면 처리기 호출
                BaseScreen(
                    uiState = uiState,
                    onRetry = { /* 재시도 로직 위치 */ }
                ) { data ->
                    // 4. 성공 시 공통 레이아웃 호출
                    BaseScaffold(title = "홈") { scaffoldPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(scaffoldPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = data)
                        }
                    }
                }
            }
            composable(Route.Category.route) {
                // 임시 카테고리 화면
                Text("카테고리 화면")
            }
            composable(Route.MyPage.route) {
                // 임시 마이페이지 화면
                Text("마이페이지 화면")
            }
        }
    }
}