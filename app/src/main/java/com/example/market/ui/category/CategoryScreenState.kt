package com.example.market.ui.category

import com.example.market.model.category.Category
import com.example.market.model.common.Product

data class CategoryScreenState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = "",
    val products: List<Product> = emptyList(),
    val isProductsLoading: Boolean = false, // 오른쪽 상품 목록만 로딩할 때 사용
    val searchQuery: String = "",
    val hasSearched: Boolean = false

)

/*
왼쪽 사이드바 목록 (categories)
현재 내가 누른 카테고리가 뭔지 (selectedCategoryId) -> 그래야 왼쪽 버튼에 불이 들어오겠죠?
오른쪽 상품 리스트 (products)
오른쪽만 로딩 중인지 (isProductsLoading) -> 왼쪽 메뉴는 가만히 있고 오른쪽만 뱅글뱅글 돌아야 하니까요.
이걸 뷰모델에서 각각 StateFlow로 만들면 UI에서 collectAsState를 4번이나 해야 하고 코드가 지저분해집니다.
그래서 "이 화면에서 쓸 상태들을 하나의 바구니(ScreenState)에 담아서 UiState.Success로 한 번에 던지자!"는 전략을 쓴 겁니다.

 */