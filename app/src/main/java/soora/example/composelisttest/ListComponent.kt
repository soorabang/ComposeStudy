package soora.example.composelisttest

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TestListView(
    count: Int,
    viewModel: TestViewModel = hiltViewModel()
) {
    val onClickItem = { index: Int -> viewModel.updateSelectedPosition(index)}
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        viewModel.uiStateFlow.collectLatest { value ->
            if (value.scrollToPosition > -1) {
                listState.animateScrollToItem(value.scrollToPosition)
            }
        }
    }
    Column() {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    viewModel.scrollToPosition(0)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Text("Top Scroll")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.scrollToPosition(count - 1)
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Bottom Scroll")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.updateSelectedPosition(-1)
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Refresh List")
            }
        }
        LazyColumn(state = listState) {
            items(count) {
                TestListItem(
                    order = it,
                    1f,
                    viewModel.uiState.selectedPosition != -1 && viewModel.uiState.selectedPosition == it,
                    viewModel.uiState.selectedPosition == -1 || viewModel.uiState.selectedPosition == it,
                    onClickItem
                )
            }
        }
    }
}

@Composable
fun TestListItem(
    order: Int,
    indicatorProgress: Float,
    selected: Boolean = false,
    focused: Boolean = false,
    onClick: (Int) -> Unit = {}
) {
    var progress by remember { mutableStateOf(0f) }
    var finished by remember { mutableStateOf(false) }

    val progressAnimDuration = 500
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        finishedListener = {
            finished = true
        }
    )

    Card(
        modifier = Modifier
            .alpha(if (focused) 1f else 0.3f)
            .padding(12.dp)
            .border(width = 4.dp, color = Color.Black)
            .fillMaxWidth()
            .height(100.dp)
            .width(300.dp)
            .clickable {
                onClick.invoke(order)
            },

    ) {
        if (selected) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp),
                backgroundColor = Color.Transparent,
                color = if (finished) Color.Transparent else Color.LightGray, //progress color,
                progress = progressAnimation
            )
            LaunchedEffect(indicatorProgress) {
                progress = indicatorProgress
            }
        }
        Box(contentAlignment = Alignment.Center) {
            Text("Compose List Text $order")
        }
    }
}

@Preview
@Composable
fun TestItemPreview() {
    TestListItem(100, 0.5f)
}

@Preview
@Composable
fun ListPreview() {
    TestListView(count = 10, TestViewModel(LocalContext.current))
}