import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.RootScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        RootScreen()
    }
}