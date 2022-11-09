package soora.example.composelisttest

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TestListView(count: Int, viewModel: TestViewModel = hiltViewModel()) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn() {
        items(count) {
            TestListItem(order = it)
        }
    }
}

@Composable
fun TestListItem(order: Int) {
    Card(
       modifier = Modifier
           .padding(12.dp)
           .border(width = 4.dp, color = Color.Black)
           .fillMaxWidth()
           .height(100.dp)
           .width(300.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Compose List Text $order")
        }
    }

}

@Preview
@Composable
fun TestItemPreview() {
    TestListItem(100)
}

@Preview
@Composable
fun ListPreview() {
    TestListView(count = 10, TestViewModel(LocalContext.current))
}