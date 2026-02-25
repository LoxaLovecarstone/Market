package com.example.market.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        uiState = uiState,
        onRetry = { viewModel.fetchHomeData() }
    ) { homeData ->
        BaseScaffold(title = "홈") { scaffoldPadding ->
            HomeContent(
                productListSize = homeData.products.size,
                modifier = Modifier.padding(scaffoldPadding)
            )
        }
    }
}

@Composable
fun HomeContent(
    productListSize: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "성공적으로 ${productListSize}개의 상품을 불러왔습니다.")
    }
}
