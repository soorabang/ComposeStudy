package soora.example.composelisttest

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TestListView(
    count: Int, viewModel: TestViewModel = hiltViewModel(), clickListener: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    if (viewModel.uiState.scrollToPosition > -1) {
        coroutineScope.launch {
            listState.animateScrollToItem(viewModel.uiState.scrollToPosition)
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                Log.e("soora", "onPreScroll ${listState.isScrollInProgress}")
                //listState.isScrollInProgress- true 계속 호출됨.  처음 한번만 처리하도록 해야함.
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                Log.e("soora", "onPostScroll")
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                Log.e("soora", "onPreFling")
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                Log.e("soora", "onPostFling  ${listState.isScrollInProgress}")
                // listState.isScrollInProgress - false
                // SCROLL_STATE_IDLE

                return super.onPostFling(consumed, available)
            }
        }
    }

    Column() {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    viewModel.scrollToPosition(0)
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), shape = RectangleShape
            ) {
                Text("Top Scroll")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.scrollToPosition(count - 1)
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), shape = RectangleShape
            ) {
                Text("Bottom Scroll")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.clearData()
                }, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), shape = RectangleShape
            ) {
                Text("Refresh List")
            }
        }
        //https://blog.onebone.me/post/jetpack-compose-nested-scroll/
        // pointerInteropFilter 는 Box에 넣든, LazyColumn에 넣든 중간에 DOWN 만 계속 찍히면서 MOVE가 먹는 현상이 있음.
        Box(Modifier.nestedScroll(nestedScrollConnection)/*.pointerInteropFilter {
            when(it.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("soora", "ACTION_DOWN")
                }
                else -> {}
            }
            true
        }*/) {
            LazyColumn(state = listState) {
                items(count) {
                    TestListItem(
                        order = it,
                        focused = viewModel.uiState.selectedPosition == -1 || viewModel.uiState.selectedPosition == it,
                        1f,
                        clickListener,
                    )
                }
            }
        }

    }
}

@Composable
fun TestListItem(
    order: Int,
    focused: Boolean = false,
    progressTargetValue: Float = 1f,
    onClick: (Int) -> Unit = {}
) {
    var progress by remember { mutableStateOf(0f) }
    var selected by remember { mutableStateOf(false) }

    val progressAnimDuration = 500
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = progressAnimDuration, easing = LinearOutSlowInEasing
        ),
        finishedListener = {
            selected = false
            progress = 0f
        }
    )

    Box(modifier = Modifier
        .alpha(if (focused) 1f else 0.3f)
        .padding(12.dp)
        .border(width = 4.dp, color = Color.Black)
        .fillMaxWidth()
        .height(100.dp)
        .width(300.dp)
        .clickable(
            // no ripple effect
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = {
                onClick.invoke(order)
                selected = true
                progress = progressTargetValue
            }
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            var listtext = "Compose List Text $order"
            if (order == 0) {
                listtext = "Go to DetailActivity"
            }
            Text(text = listtext, textAlign = TextAlign.Center)
        }

        if (selected) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .alpha(if (selected) 0.5f else 0f),
                trackColor = Color.Transparent,
                color = if (selected) Color.LightGray else Color.Transparent, //progress color,
                progress = progressAnimation
            )
        }
    }
}

@Preview
@Composable
fun TestItemPreview() {
    TestListItem(20)
}

@Preview
@Composable
fun ListPreview() {
    TestListView(count = 10, TestViewModel(LocalContext.current))
}