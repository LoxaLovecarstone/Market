import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.market.ui.common.BaseScaffold
import com.example.market.ui.common.BaseScreen
import com.example.market.ui.mypage.MyPageViewModel

@Composable
fun OrderListScreen(
    type: String, // "shipping" 또는 "delivered"
    viewModel: MyPageViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(uiState = uiState) { state ->
        val products = if (type == "shipping") state.shippingProducts else state.deliveredProducts
        val title = if (type == "shipping") "배송 중 상품" else "배송 완료 상품"

        BaseScaffold(
            title = title,
            showBackButton = true,
            onBackClick = onBackClick
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    // 가로형 리스트로 보여주기 위해 간단히 구성
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
                                Text(text = "${product.price}원", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}