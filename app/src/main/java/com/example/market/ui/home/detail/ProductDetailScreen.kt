package com.example.market.ui.home.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.market.model.common.Product
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        uiState = uiState,
        onRetry = { viewModel.fetchProductDetail() }
    ) { product ->
        BaseScaffold(
            title = "상품 상세",
            showBackButton = true, // 뒤로가기 버튼 활성화 (BaseScaffold에 구현 필요)
            onBackClick = onBackClick
        ) { padding ->
            ProductDetailContent(product = product, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun ProductDetailContent(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${String.format("%,d", product.price)}원",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 장바구니 버튼 (나중에 기능 추가)
            Button(
                onClick = { /* 장바구니 담기 로직 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("장바구니 담기")
            }
        }
    }
}