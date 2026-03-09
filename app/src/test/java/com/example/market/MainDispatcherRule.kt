package com.example.market

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class) // 1. 실험적 API 사용 승인
class MainDispatcherRule( // 2. 클래스 선언 및 생성자
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() { // 3. JUnit 4의 TestWatcher 상속

    // 4. 테스트 시작 직전에 실행되는 함수
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher) // 5. 메인 디스패처를 가짜로 교체
    }

    // 6. 테스트 종료 직후에 실행되는 함수
    override fun finished(description: Description) {
        Dispatchers.resetMain() // 7. 메인 디스패처를 원래대로 복구
    }
}