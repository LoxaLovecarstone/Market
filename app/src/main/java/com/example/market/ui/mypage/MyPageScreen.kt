package com.example.market.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel = hiltViewModel(),
    onOrderClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(uiState = uiState) { state ->
        BaseScaffold(title = "내 정보") { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // 1. 프로필 영역
                ProfileSection(state.name, state.email, state.profileImageUrl)

                Spacer(modifier = Modifier.height(32.dp))

                // 2. 주문 현황 영역
                Text(text = "주문 현황", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OrderRow(label = "배송 중", count = state.shippingProducts.size) { onOrderClick("shipping") }
                OrderRow(label = "배송 완료", count = state.deliveredProducts.size) { onOrderClick("delivered") }}
        }
    }
}

@Composable
fun ProfileSection(name: String, email: String, imageUrl: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "프로필 이미지",
            modifier = Modifier.size(80.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "${name}님", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun OrderRow(label: String, count: Int, onClick: () -> Unit) {
    // Surface에 onClick이 달려 있어 클릭 시 물결 효과(Ripple)가 발생합니다.
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent // 배경색은 투명하게 유지
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${count}건",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}