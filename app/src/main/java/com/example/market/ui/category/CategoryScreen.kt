package com.example.market.ui.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.market.model.category.Category
import com.example.market.model.common.Product
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen
import com.example.market.ui.home.ProductItem

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit // 상품 클릭 시 상세로 이동하기 위한 콜백
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(uiState = uiState, onRetry = { }) { state ->
        BaseScaffold(title = "카테고리") { padding ->
            // Row를 사용하여 화면을 좌/우로 나눕니다.
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. 왼쪽 사이드바 (너비 고정)
                CategorySidebar(
                    categories = state.categories,
                    selectedCategoryId = state.selectedCategoryId,
                    onCategoryClick = { categoryId ->
                        viewModel.fetchProductsByCategory(categoryId)
                    },
                    modifier = Modifier.width(100.dp).fillMaxHeight()
                )

                // 구분선 하나 넣어주면 깔끔하죠.
                VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // 2. 오른쪽 상품 리스트 영역 (남은 공간 모두 차지)
                CategoryProductContent(
                    products = state.products,
                    isLoading = state.isProductsLoading,
                    onProductClick = onProductClick,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}


@Composable
fun CategorySidebar(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        // 사이드바 전체 배경색을 살짝 다르게 주면 구분이 더 잘 됩니다.
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            CategoryItem(
                category = category,
                isSelected = isSelected,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 선택 여부에 따라 배경색과 텍스트 색상을 결정합니다.
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else null
            )
        }
    }
}

@Composable
fun CategoryProductContent(
    products: List<Product>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            // 1. 로딩 중일 때 표시 (오른쪽 영역 중앙에 배치)
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (products.isEmpty()) {
            // 2. 상품이 없을 때 (혹시 모를 예외 처리)
            Text(
                text = "등록된 상품이 없습니다.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // 3. 상품 그리드 리스트
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2열 그리드
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    // 💡 홈 화면에서 만들었던 ProductItem 컴포저블을 재사용합니다!
                    ProductItem(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}