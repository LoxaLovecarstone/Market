package com.example.market.ui.mypage

import androidx.lifecycle.ViewModel
import com.example.market.model.common.Product
import com.example.market.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<MyPageUiState>>(
        UiState.Success(
            MyPageUiState(
                shippingProducts = listOf(
                    Product("1", "유기농 사과", 15000, "https://picsum.photos/id/10/400", "fruit"),
                    Product("2", "신선한 우유", 3500, "https://picsum.photos/id/20/400", "dairy")
                ),
                deliveredProducts = listOf(
                    Product("3", "통밀 식빵", 4800, "https://picsum.photos/id/30/400", "bakery"),
                    Product("4", "맛있는 닭가슴살", 12000, "https://picsum.photos/id/40/400", "meat"),
                    Product("5", "꿀 참외", 8000, "https://picsum.photos/id/11/400", "fruit"),
                    Product("6", "플레인 요거트", 4200, "https://picsum.photos/id/21/400", "dairy"),
                    Product("8", "제주 흑돼지", 38000, "https://picsum.photos/id/41/400", "meat")
                )
            )
        )
    )
    val uiState: StateFlow<UiState<MyPageUiState>> = _uiState.asStateFlow()
}