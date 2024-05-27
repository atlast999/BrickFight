package presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScreenWrapper(
    isLoading: Boolean = false,
    title: String = "",
    navigationAction: () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
        )
    },
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    LaunchedEffect(key1 = true) {
        Napier.i("Start screen: $title")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier,
                        onClick = navigationAction,
                    ) {
                        navigationIcon.invoke()
                    }
                },
                actions = actions,
            )
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize().padding(it).padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    content.invoke(this)
                }
            }
        },
    )
}

@Composable
internal fun RootScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.name,
    ) {
        composable(Screen.Login.name) {

            Text(text = "Login")
            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.Signup.name)
                }
            ) {
                Text(text = "Signup")
            }
        }

        composable(Screen.Signup.name) {
            Text(text = "Signup")
        }
    }
}

enum class Screen(val title: String) {
    Login(title = "Login"),
    Signup(title = "Signup")
}