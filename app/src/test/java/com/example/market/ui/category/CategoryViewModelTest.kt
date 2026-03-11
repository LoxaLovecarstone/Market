package com.example.market.ui.category

import app.cash.turbine.test
import com.example.market.MainDispatcherRule
import com.example.market.model.category.Category
import com.example.market.model.common.Product
import com.example.market.repository.HomeRepository
import com.example.market.ui.common.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * CategoryViewModel의 핵심 비즈니스 로직(카테고리 변경 시 초기화 등)을 검증하는 테스트 클래스입니다.
 */
class CategoryViewModelTest {

    // 1. [JUnit 4 Rule] 안드로이드 메인 스레드(Main Dispatcher) 환경을 테스트용으로 교체합니다.
    // 비동기 작업인 코루틴이 테스트 환경의 가짜 스레드에서 즉시 실행되도록 돕습니다.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 2. [SUT & Mock] 테스트 대상(ViewModel)과 가짜 의존성 객체(Repository)를 선언합니다.
    private lateinit var viewModel: CategoryViewModel
    private val repository: HomeRepository = mockk()

    // 테스트에 사용할 고정된 카테고리 데이터입니다.
    // ViewModel 내부 로직상 리스트가 비어있으면 Success 상태로 진입하지 않으므로 최소 한 개가 필요합니다.
    private val mockCategories = listOf(
        Category(id = "all", name = "전체", iconUrl = "")
    )

    // 3. [Setup] 각 테스트 메서드가 실행되기 전에 공통적으로 수행할 환경 설정입니다.
    @Before
    fun setUp() {
        // 리포지토리의 suspend 함수들이 호출될 때 반환할 가짜 결과값(Mock)을 미리 정의합니다.
        // coEvery는 코루틴 전용 모킹 함수입니다.
        coEvery { repository.getCategories() } returns Result.success(mockCategories)
        coEvery { repository.getProductsByCategory(any()) } returns Result.success(emptyList())
        coEvery { repository.searchProducts(any(), any()) } returns Result.success(emptyList())

        // 가짜 리포지토리를 주입하며 테스트 대상을 초기화합니다.
        viewModel = CategoryViewModel(repository)
    }

    @Test
    fun `카테고리를 클릭하면 검색어와 검색 완료 상태가 초기화되어야 한다`() = runTest {
        // [Given] 상황 설정: 사용자가 특정 카테고리에서 "사과"를 검색하여 결과가 나온 상태를 재현합니다.
        viewModel.onQueryChanged("사과")
        viewModel.performSearch()

        // [Then] 결과 검증: Turbine 라이브러리를 사용하여 StateFlow의 상태 변화를 실시간으로 추적합니다.
        viewModel.uiState.test {
            // ① 구독 시점 상태 소비:
            // StateFlow는 구독하자마자 현재 값(사과 검색 완료 상태)을 뱉습니다. 이를 먼저 확인하거나 소비해야 합니다.
            awaitItem()

            // [When] 핵심 액션: 사용자가 다른 카테고리("dairy")를 클릭합니다.
            viewModel.onCategoryClick("dairy")

            // ② 첫 번째 상태 변화:
            // onCategoryClick 내부의 _uiState.update에 의해 searchQuery와 hasSearched가 즉시 초기화됩니다.
            val stateAfterClick = awaitItem()

            // ③ 두 번째 상태 변화 처리:
            // 뷰모델 로직상 onCategoryClick 직후 fetchProductsByCategory가 실행되며 setLoading(true)가 호출됩니다.
            // 연속적인 업데이트로 인해 발생한 '로딩 중' 상태를 건너뛰거나 최종 상태를 낚아챕니다.
            val finalState = if (stateAfterClick is UiState.Success && stateAfterClick.data.isProductsLoading) {
                // 제품 목록을 가져오는 중인 상태라면, 그 다음으로 오는 Success(완료) 아이템을 기다립니다.
                awaitItem()
            } else {
                stateAfterClick
            }

            // [Assert] 최종 검증: 상태가 Success인지 확인하고, 내부 데이터가 비즈니스 규칙대로 초기화되었는지 검사합니다.
            assertTrue("현재 상태가 Success가 아닙니다.", finalState is UiState.Success)
            val data = (finalState as UiState.Success).data

            // 카테고리 이동 시 검색어는 반드시 비워져야 합니다.
            assertEquals("", data.searchQuery)
            // 검색 완료 여부도 다시 거짓(false)으로 돌아가야 합니다.
            assertEquals(false, data.hasSearched)
            // 선택된 카테고리 ID는 클릭한 대로 변경되어야 합니다.
            assertEquals("dairy", data.selectedCategoryId)

            // 테스트 종료 후 남아있는 불필요한 이벤트들을 무시하고 관찰을 종료합니다.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `초기 카테고리 목록을 가져오는 데 실패하면 상태가 Error가 되어야 한다`() = runTest {
        // [Given] 리포지토리가 실패를 반환하도록 설정
        coEvery { repository.getCategories() } returns Result.failure(Exception("네트워크 에러"))

        // [When] 뷰모델 생성
        val viewModel = CategoryViewModel(repository)

        // [Then] 상태 검증
        viewModel.uiState.test {
            // expectMostRecentItem()은 중간의 Loading 등을 건너뛰고
            // 현재 시점의 가장 마지막 상태를 가져옵니다.
            val finalState = expectMostRecentItem()

            assertTrue("현재 상태가 Error여야 합니다.", finalState is UiState.Error)
        }
    }

    @Test
    fun `검색어를 입력하고 검색을 수행하면 리포지토리에 올바른 인자가 전달되어야 한다`() = runTest {
        // [Given] 가짜 데이터 및 리포지토리 설정
        val mockProducts = listOf(
            Product("p1", "사과", 3000, "", "all")
        )

        // 정확한 인자가 들어올 때만 결과를 반환하도록 설정
        coEvery {
            repository.searchProducts(query = "사과", categoryId = "all")
        } returns Result.success(mockProducts)

        viewModel.uiState.test {
            // 1. 초기 상태 소비
            awaitItem()

            // [When] 검색어 입력
            viewModel.onQueryChanged("사과")

            // 2. 검색어 반영 상태 낚아채기
            val stateAfterQuery = awaitItem()
            assertEquals("사과", (stateAfterQuery as UiState.Success).data.searchQuery)

            // [When] 검색 실행
            viewModel.performSearch()

            // 3. 병합(Conflation)을 고려하여 최종 상태만 확인
            // 로딩 상태가 너무 빨리 지나가서 awaitItem()이 안 잡힐 때는
            // 최종 상태의 data가 우리가 원하는 값인지 확인하면 됩니다.
            val finalState = expectMostRecentItem()

            assertTrue(finalState is UiState.Success)
            val data = (finalState as UiState.Success).data

            // 데이터 검증
            assertEquals(mockProducts, data.products)
            assertEquals(true, data.hasSearched)
            assertEquals(false, data.isProductsLoading) // 로딩이 무사히 끝났는지 확인

            // [Verify] 실제로 리포지토리가 "사과", "all"로 호출되었는지 최종 확인
            coVerify { repository.searchProducts("사과", "all") }

            // coVerify는 코루틴 함수가 호출되었는지 확인
            // 파라미터 verifyBlock 에는 "해당 코루틴 함수가 실제로 호출된 적이 있는지"를 확인하는 것이 들어간다.

            cancelAndIgnoreRemainingEvents()
        }
    }
}