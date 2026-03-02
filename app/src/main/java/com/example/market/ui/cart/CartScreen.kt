package com.example.market.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.market.model.cart.CartItemUiModel
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(uiState = uiState, onRetry = { }) { cartItems ->
        BaseScaffold(
            title = "장바구니",
            showBackButton = true,
            onBackClick = onBackClick
        ) { padding ->
            // 💡 전체 구조를 Column으로 잡고 하단 바를 배치합니다.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. 장바구니 콘텐츠 (리스트 또는 빈 화면)
                CartContent(
                    cartItems = cartItems,
                    modifier = Modifier.weight(1f), // 남은 공간을 모두 차지
                    onQuantityChange = { productId, newQuantity ->
                        viewModel.updateQuantity(productId, newQuantity)
                    },
                    onDeleteClick = { productId ->
                        viewModel.deleteItem(productId)
                    },
                    onNavigateToHome = onBackClick // 쇼핑하러 가기 클릭 시 뒤로가기
                )

                // 2. 합계 금액 영역 (리스트가 비어 있지 않을 때만 노출)
                if (cartItems.isNotEmpty()) {
                    CartTotalSummary(cartItems = cartItems)
                }
            }
        }
    }
}

@Composable
fun CartContent(
    cartItems: List<CartItemUiModel>,
    modifier: Modifier = Modifier,
    onQuantityChange: (String, Int) -> Unit, // 💡 String(ID)과 Int(수량)를 받는 파라미터 추가
    onDeleteClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    if (cartItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "장바구니가 비어 있습니다.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateToHome) {
                    Text("쇼핑하러 가기")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onQuantityChange = { quantity -> onQuantityChange(item.product.id, quantity) },
                    onDeleteClick = { onDeleteClick(item.product.id) }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemUiModel,
    onQuantityChange: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) }) {
                        Text("-")
                    }
                    Text(text = "${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Text("+")
                    }
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "삭제")
            }
        }
    }
}

@Composable
fun CartTotalSummary(cartItems: List<CartItemUiModel>) {
    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding() // 시스템 네비게이션 바 영역 확보
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 결제 예정 금액",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%,d", totalPrice)}원",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* 주문 기능은 추후 구현 */ },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(
                    text = "주문하기",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}