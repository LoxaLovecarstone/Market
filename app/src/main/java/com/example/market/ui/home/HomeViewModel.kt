package com.example.market.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.home.HomeData
import com.example.market.repository.HomeRepository
import com.example.market.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository // Hilt가 알아서 주입해줌
) : ViewModel() {

    // 화면의 상태를 담는 StateFlow
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // 저장소에서 데이터를 가져옴
            val result = repository.getHomeData()

            // Result 결과에 따라 상태 변경
            result.onSuccess { data ->
                _uiState.value = UiState.Success(data)
            }.onFailure {
                _uiState.value = UiState.Error
            }
        }
    }
}