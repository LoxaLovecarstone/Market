package com.example.market.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.market.model.cart.CartItem
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // ① 안드로이드 JUnit 4 주행기 사용
class CartDaoTest {

    // ② LiveData나 룸의 비동기 작업을 순차적으로 실행하게 만드는 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: MarketDatabase
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        // ③ 인메모리 DB 생성: 테스트 종료 후 메모리에서 자동 삭제됨 (실제 DB 영향 X)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MarketDatabase::class.java
        ).allowMainThreadQueries() // 테스트 편의를 위해 메인 스레드 쿼리 허용
            .build()

        cartDao = database.cartDao()
    }

    @After
    fun tearDown() {
        // ④ 테스트 종료 후 DB 닫기
        database.close()
    }

    @Test
    fun 장바구니에_아이템을_넣으면_Flow가_새로운_리스트를_뱉어야_한다() = runTest {
        // [Given] 테스트용 데이터 준비
        val item = CartItem(productId = "p1", quantity = 2)

        // [Then] Turbine 라이브러리로 Flow 관찰 시작
        cartDao.getAllCartItems().test {
            // 1. 처음에는 장바구니가 비어있어야 함
            assertEquals(emptyList<CartItem>(), awaitItem())

            // [When] 아이템 삽입
            cartDao.insertCartItem(item)

            // 2. 삽입 후 새롭게 갱신된 리스트 확인
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("p1", items[0].productId)
            assertEquals(2, items[0].quantity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun 같은_아이템을_다른_수량으로_다시_넣으면_데이터가_교체되어야_한다() = runTest {
        val item1 = CartItem(productId = "p1", quantity = 2)
        val item2 = CartItem(productId = "p1", quantity = 5) // 같은 ID, 다른 수량

        cartDao.getAllCartItems().test {
            awaitItem() // 초기 빈 리스트 소비

            // 1. 처음 아이템 삽입
            cartDao.insertCartItem(item1)
            assertEquals(2, awaitItem()[0].quantity)

            // 2. 같은 ID의 아이템을 수량만 바꿔서 다시 삽입
            cartDao.insertCartItem(item2)

            // [Assert] 리스트 크기는 여전히 1개여야 하고, 수량은 5로 바뀌어 있어야 함
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(5, items[0].quantity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun 장바구니에서_아이템을_삭제하면_목록에서_사라져야_한다() = runTest {
        val item = CartItem(productId = "p1", quantity = 1)

        // 1. 먼저 하나를 넣습니다.
        cartDao.insertCartItem(item)

        cartDao.getAllCartItems().test {
            // 넣은 직후의 상태 확인 (리스트에 p1이 있음)
            val currentItems = awaitItem()
            assertEquals(1, currentItems.size)

            // [When] 아이템 삭제 실행
            cartDao.deleteCartItem(item)

            // [Then] 리스트가 다시 비어있는지 확인
            val itemsAfterDelete = awaitItem()
            assertTrue("삭제 후에는 리스트가 비어있어야 합니다.", itemsAfterDelete.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}