package com.example.market.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
            showBackButton = true, // 뒤로가기 화살표 표시
            onBackClick = onBackClick
        ) { padding ->
            // 장바구니 리스트 UI 구현부
            CartContent(cartItems = cartItems, modifier = Modifier.padding(padding))
        }
    }
}


// ui/cart/CartScreen.kt 내부 하단에 추가
@Composable
fun CartContent(
    cartItems: List<CartItemUiModel>,
    modifier: Modifier = Modifier,
    onDeleteClick: (String) -> Unit = {} // 삭제 로직이 필요하다면 추가
) {
    if (cartItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("장바구니가 비어 있습니다.")
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
                    onDeleteClick = { onDeleteClick(item.product.id) }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemUiModel, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.product.imageUrl, contentDescription = null, modifier = Modifier.size(80.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${item.quantity}개", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "삭제")
            }
        }
    }
}