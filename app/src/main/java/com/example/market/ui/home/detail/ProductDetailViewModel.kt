package com.example.market.ui.home.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.common.Product
import com.example.market.repository.CartRepository
import com.example.market.repository.HomeRepository
import com.example.market.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository, // 인터페이스 주입
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Route에서 설정한 "productId"라는 이름으로 인자를 가져옵니다.
    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<String>() // 알림 메시지를 위한 이벤트 스트림
    val event = _event.asSharedFlow()

    init {
        fetchProductDetail()
    }

    fun fetchProductDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getProductById(productId)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error }
        }
    }


    fun addToCart() {
        viewModelScope.launch {
            val currentItems = cartRepository.getCartItems().first() // Flow의 현재 값을 한 번 가져옴
            val isExisted = currentItems.any { it.product.id == productId }

            if (isExisted) {
                _event.emit("이미 장바구니에 담긴 상품입니다.")
            } else {
                cartRepository.addToCart(productId)
                _event.emit("장바구니에 담았습니다.")
            }
        }
    }
}