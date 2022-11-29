package soora.example.composelisttest

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    var uiState by mutableStateOf(UiState())

    //외부에서 compose list의 동작을 변경
    fun scrollToPosition(position: Int) {
        updateScrollToPosition(position) // actual scroll
        updateSelectedPosition(position) // focused ui
    }

    fun updateSelectedPosition(index: Int) {
        uiState = uiState.copy(selectedPosition = index)
    }

    private fun updateScrollToPosition(position: Int) {
        uiState = uiState.copy(scrollToPosition = position)
    }

    fun updateProgressValue(value: Float) {
        uiState = uiState.copy(progressValue = value)
    }

    fun clearData() {
        updateSelectedPosition(-1)
        updateScrollToPosition(-1)
    }

}