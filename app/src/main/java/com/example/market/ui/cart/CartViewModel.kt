package com.example.market.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.cart.CartItemUiModel
import com.example.market.repository.CartRepository
import com.example.market.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CartItemUiModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CartItemUiModel>>> = _uiState.asStateFlow()

    init {
        fetchCartItems()
    }

    private fun fetchCartItems() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _uiState.value = if (items.isEmpty()) UiState.Error else UiState.Success(items)
            }
        }
    }

    fun deleteItem(productId: String) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(productId)
        }
    }
}