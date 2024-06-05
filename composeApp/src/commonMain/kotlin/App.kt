import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import di.appModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import presentation.RootScreen
import theme.AppTheme

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModules())
        }
    ) {
        DisposableEffect(true) {
            Napier.base(DebugAntilog())
            onDispose {
                Napier.d("Stopping koin application")
                stopKoin()
            }
        }
        AppTheme {
            RootScreen()
        }
    }
}