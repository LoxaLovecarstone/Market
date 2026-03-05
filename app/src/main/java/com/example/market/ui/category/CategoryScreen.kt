package com.example.market.ui.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.market.model.category.Category
import com.example.market.model.common.Product
import com.example.market.ui.common.BaseScreen
import com.example.market.ui.common.UiState
import com.example.market.ui.home.ProductItem

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // 검색창이 포함된 커스텀 Scaffold 구성
    Scaffold(
        topBar = {
            CategorySearchTopBar(
                query = (uiState as? UiState.Success)?.data?.searchQuery ?: "",
                onQueryChanged = viewModel::onQueryChanged,
                onSearch = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                }
            )
        }
    ) { padding ->
        BaseScreen(uiState = uiState, onRetry = { viewModel.performSearch() }) { state ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. 왼쪽 사이드바
                CategorySidebar(
                    categories = state.categories,
                    selectedCategoryId = state.selectedCategoryId,
                    onCategoryClick = { categoryId ->
                        viewModel.onCategoryClick(categoryId)
                    },
                    modifier = Modifier.width(100.dp).fillMaxHeight()
                )

                VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // 2. 오른쪽 상품 콘텐츠 영역
                CategoryProductContent(
                    products = state.products,
                    isLoading = state.isProductsLoading,
                    hasSearched = state.hasSearched,
                    searchQuery = state.searchQuery,
                    onProductClick = onProductClick,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchTopBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                placeholder = { Text("카테고리 내 검색", style = MaterialTheme.typography.bodyMedium) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "지우기")
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun CategoryProductContent(
    products: List<Product>,
    isLoading: Boolean,
    hasSearched: Boolean,
    searchQuery: String,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (products.isEmpty()) {
            val emptyMessage = if (hasSearched) "'$searchQuery' 검색 결과가 없습니다." else "상품이 존재하지 않습니다."
            Text(text = emptyMessage, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductItem(product = product, onClick = { onProductClick(product.id) })
                }
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
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}