package com.example.market.data.repository

import com.example.market.data.local.CartDao
import com.example.market.model.cart.CartItem
import com.example.market.model.cart.CartItemUiModel
import com.example.market.model.common.Product
import com.example.market.repository.CartRepository
import com.example.market.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.find

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val homeRepository: HomeRepository
) : CartRepository {

    // 상품 정보를 메모리에 임시로 저장합니다.
    private var cachedProducts: List<Product>? = null


    // 나중에 List<CartItemUiModel>를 줄줄이 내뱉는 통로
    override fun getCartItems(): Flow<List<CartItemUiModel>> = flow {  // 이 중괄호 { }는 데이터를 어떻게 생산할지 정의하는 suspend 블록

        // 단계 1: 파이프라인 형성 (준비 단계)
        // 뷰모델에서 .collect()를 호출하는 순간, 이 블록 내부의 코드가 실행되기 시작합니다.

        // 단계 2: 상품 마스터 데이터 로드 (최초 1회 지연)
        if (cachedProducts == null) {
            // 여기서 homeRepository.getHomeData()가 실행되며 내부의 delay(1500) 때문에 1.5초간 멈춥니다.
            val homeData = homeRepository.getHomeData().getOrNull()
            cachedProducts = homeData?.products ?: emptyList()
            // 이제 cachedProducts에 상품 정보가 들어왔으므로 다음 호출부터는 이 if문은 건너뜁니다.
        }

        // 단계 3: 데이터베이스 관찰 시작 (DAO 구독)
        // cartDao.getAllCartItems()는 DB가 변할 때마다 새 값을 내보내는 Flow입니다.
        // 여기서 .collect { ... }를 호출하면, 이 flow { } 블록은 여기서 '대기 상태'로 들어갑니다.
        cartDao.getAllCartItems().collect { cartEntities ->

            // 단계 4: 데이터 가공 (ID -> 실물 상품 매칭)
            // DB에서 넘어온 '아이디 리스트(cartEntities)'를 아까 저장해둔 '상품 정보(cachedProducts)'와 합칩니다.
            // 이 과정은 메모리 상에서 일어나기 때문에 지연 시간이 거의 0에 가깝습니다.
            val uiModels = cartEntities.mapNotNull { entity ->
                val product = cachedProducts?.find { it.id == entity.productId }
                product?.let { CartItemUiModel(it, entity.quantity) }
            }

            // 단계 5: 최종 데이터 방출 (ViewModel로 전달)
            // 가공이 끝난 uiModels를 emit(방출)합니다.
            // 그러면 이 Flow를 구독 중인 ViewModel과 UI가 이 값을 받아 화면을 즉시 갱신합니다.
            emit(uiModels)  // 결과 전송: "가공 다 됐으니 통로로 던져!"

            // 💡 중요 포인트:
            // cartDao.getAllCartItems()는 DB가 수정될 때마다 자동으로 새로운 cartEntities를 던져줍니다.
            // 그러면 이 단계 4~5 과정이 '무한 반복'되며 실시간 업데이트가 일어나는 구조입니다.
        }
    }

    override suspend fun addToCart(productId: String) {
        cartDao.insertCartItem(CartItem(productId = productId))
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        cartDao.insertCartItem(CartItem(productId = productId, quantity = quantity))
    }

    override suspend fun deleteCartItem(productId: String) {
        cartDao.deleteCartItem(CartItem(productId = productId))
    }
}