package com.example.market.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffold(
    title: String = "",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},  // () -> Unit인 Composable을 파라미터로 받고, 주어지지 않는다면 {}을 기본으로 함
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit // PaddingValues를 파라미터로 받아 Unit을 리턴하는 Composable을 파라미터로 받음
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = { innerPadding ->
            content(innerPadding)
        }
    )
}