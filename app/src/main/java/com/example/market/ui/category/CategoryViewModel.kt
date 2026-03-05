package com.example.market.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.repository.HomeRepository
import com.example.market.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CategoryScreenState>>(UiState.Loading)
    val uiState: StateFlow<UiState<CategoryScreenState>> = _uiState.asStateFlow()

    init {
        fetchCategories()
    }

    // 1. 전체 카테고리 목록 가져오기
    private fun fetchCategories() {
        viewModelScope.launch {
            repository.getCategories().onSuccess { categories ->
                if (categories.isNotEmpty()) {
                    // 카테고리가 있으면 상태를 업데이트하고 첫 번째 카테고리 상품 로드
                    val firstCategoryId = categories[0].id
                    _uiState.value = UiState.Success(
                        CategoryScreenState(
                            categories = categories,
                            selectedCategoryId = firstCategoryId
                        )
                    )
                    fetchProductsByCategory(firstCategoryId)
                }
            }.onFailure {
                _uiState.value = UiState.Error
            }
        }
    }

    // 2. 특정 카테고리의 상품 가져오기 (클릭 시마다 호출)
    fun onCategoryClick(categoryId: String) {
        _uiState.update { state ->
            if (state is UiState.Success) {
                // 카테고리가 바뀌면 검색어와 검색 기록을 초기화하는 것이 일반적입니다.
                UiState.Success(state.data.copy(
                    selectedCategoryId = categoryId,
                    searchQuery = "",
                    hasSearched = false
                ))
            } else state
        }
        fetchProductsByCategory(categoryId)
    }

    private fun fetchProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            setLoading(true)
            repository.getProductsByCategory(categoryId).onSuccess { products ->
                updateSuccessState(products, false)
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { state ->
            if (state is UiState.Success) {
                UiState.Success(state.data.copy(searchQuery = query))
            } else state
        }
    }

    fun performSearch() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            setLoading(true)
            repository.searchProducts(
                query = currentState.searchQuery,
                categoryId = currentState.selectedCategoryId
            ).onSuccess { products ->
                updateSuccessState(products, true)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update {
            if (it is UiState.Success) UiState.Success(it.data.copy(isProductsLoading = isLoading)) else it
        }
    }

    private fun updateSuccessState(products: List<com.example.market.model.common.Product>, hasSearched: Boolean) {
        _uiState.update { state ->
            if (state is UiState.Success) {
                UiState.Success(state.data.copy(
                    products = products,
                    isProductsLoading = false,
                    hasSearched = hasSearched
                ))
            } else state
        }
    }
}