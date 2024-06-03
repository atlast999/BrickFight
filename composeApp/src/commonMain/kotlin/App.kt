import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.RootScreen

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        Napier.base(DebugAntilog())
    }
    MaterialTheme {
        RootScreen()
    }
}