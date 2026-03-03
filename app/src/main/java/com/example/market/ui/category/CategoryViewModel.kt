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
    fun fetchProductsByCategory(categoryId: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            // 상품 목록 영역만 로딩 상태로 변경
            _uiState.update {
                if (it is UiState.Success) {
                    UiState.Success(it.data.copy(
                        selectedCategoryId = categoryId,
                        isProductsLoading = true
                    ))
                } else it
            }

            // 리포지토리에서 해당 카테고리 상품 조회
            repository.getProductsByCategory(categoryId).onSuccess { products ->
                _uiState.update {
                    if (it is UiState.Success) {
                        UiState.Success(it.data.copy(
                            products = products,
                            isProductsLoading = false
                        ))
                    } else it
                }
            }.onFailure {
                // 에러 처리 로직 (필요 시)
                _uiState.update {
                    if (it is UiState.Success) {
                        UiState.Success(it.data.copy(isProductsLoading = false))
                    } else it
                }
            }
        }
    }
}