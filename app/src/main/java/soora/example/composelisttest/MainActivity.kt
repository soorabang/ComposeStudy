package soora.example.composelisttest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import soora.example.composelisttest.ui.theme.ComposeListTestTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var testViewModel: TestViewModel
    private val clickListItem = { index: Int ->
        testViewModel.updateSelectedPosition(index)
        testViewModel.updateProgressValue(1f)

        if (index == 0) {
            val intent = Intent(this, DetailActivity::class.java)
            this.startActivity(intent)
        }
    }


    private val scrollListener = { state: Int ->
        var text = when (state) {
            1 -> "Start Scroll or Touch"
            2 -> "Scroll Stopped"
            else -> "Nothing"
        }
        Log.e("soora", "scrollListener $text")
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestListView(count = 20, testViewModel, clickListItem, scrollListener)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeListTestTheme {
        Greeting("Android")
    }
}