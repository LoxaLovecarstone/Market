package com.example.market.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> BaseScreen(  // BaseScreen은 제네릭임. 어떤 타입이 올지는 모르지만 일단 T라고 하자.
    uiState: UiState<T>,  // UiState가 쓸 타입과 BaseScreen이 같은 타입을 다루는 것을 명시
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit  // 성공(Success) 상태일 때만 실행될 UI 코드 조각.
    // 성공해서 꺼낸 데이터(T)를 파라미터로 넘겨줌으로써, 사용하는 쪽에서 데이터를 편하게 쓰게 함
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UiState.Loading -> {
                // 로딩 중일 때 보여줄 뺑글이
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                // 에러 발생 시 보여줄 화면
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "문제가 발생했습니다.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "다시 시도")
                    }
                }
            }
            is UiState.Success -> {  // 컴파일러가 UiState를 Success 타입으로 취급
                // 성공 시 실제 콘텐츠를 그림
                content(uiState.data)
            }
        }
    }
}