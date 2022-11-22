package soora.example.composelisttest

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SlideAnimation(isVisible: Boolean) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(isVisible) {
        visible = isVisible
    }
    AnimatedVisibility(
        visible = visible,
        exit = slideOutHorizontally(
            targetOffsetX = { 1000 },
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Hello",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))
            Button(modifier = Modifier
                .padding(20.dp)
                .height(60.dp), onClick = { visible = false }) {
                Text("Slide Out", style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
fun AlphaAnimation(isVisible: Boolean) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        visible = isVisible
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        Spacer(Modifier.height(50.dp))
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(
                    durationMillis = 3000
                ),
            ) + slideInVertically (initialOffsetY = {50}, animationSpec = tween(durationMillis = 3000))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_completed),
                contentDescription = "send complete",
                modifier = Modifier
                    .width(93.3.dp)
                    .height(93.3.dp)
            )
        }
        Spacer(Modifier.height(30.dp))
        Button(modifier = Modifier
            .padding(20.dp)
            .height(60.dp), onClick = { visible = true }) {
            Text("Animation Alpha", style = MaterialTheme.typography.body1)
        }
    }
}