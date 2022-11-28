package soora.example.composelisttest

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    viewModel: TestViewModel = hiltViewModel(),
    clickListener: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    if (viewModel.uiState.scrollToPosition > -1) {
        coroutineScope.launch {
            listState.animateScrollToItem(viewModel.uiState.scrollToPosition)
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
                    viewModel.clearData()
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
                    viewModel.uiState.selectedPosition != -1 && viewModel.uiState.selectedPosition == it,
                    viewModel.uiState.selectedPosition == -1 || viewModel.uiState.selectedPosition == it,
                    viewModel.uiState.progressValue,
                    clickListener,
                )
            }
        }
    }
}

@Composable
fun TestListItem(
    order: Int,
    selected: Boolean = false,
    focused: Boolean = false,
    progressTargetValue: Float = 0f,
    onClick: (Int) -> Unit = {}
) {
    var progress by remember { mutableStateOf(0f) }
    var finished by remember { mutableStateOf(false) }

    val progressAnimDuration = 500
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = LinearOutSlowInEasing),
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
            .clickable(
                // no ripple effect
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    progress = 0f
                    finished = false
                    onClick.invoke(order)
                }
            )

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
            LaunchedEffect(progressTargetValue) {
                progress = progressTargetValue
            }
        }
        Box(contentAlignment = Alignment.Center) {
            var listtext = "Compose List Text $order"
            if (order == 0) {
                listtext = "Go to DetailActivity"
            }
            Text(listtext)
        }
    }
}

@Preview
@Composable
fun TestItemPreview() {
    TestListItem(20, selected = true, focused = true)
}

@Preview
@Composable
fun ListPreview() {
    TestListView(count = 10, TestViewModel(LocalContext.current))
}