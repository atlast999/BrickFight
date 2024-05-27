package presentation

import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.Napier
import presentation.authentication.LoginUI
import presentation.authentication.LoginViewModel

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
        enterTransition = {
            slideIn(initialOffset = {
                IntOffset(it.width, 0)
            })
        },
        exitTransition = {
            slideOut(targetOffset = {
                IntOffset(it.width, 0)
            })
        }
    ) {
        composable(Screen.Login.name) {
            val viewModel = viewModel<LoginViewModel>(
                factory = remember {
                    viewModelFactory {
                        initializer {
                            LoginViewModel()
                        }
                    }
                }
            )
            val uiState by viewModel.state.collectAsState()
            ScreenWrapper(
                title = Screen.Login.title,
                navigationIcon = {},
            ) {
                LoginUI(
                    username = uiState.username,
                    onUsernameChanged = viewModel::onUsernameChanged,
                    onLoginClicked = {
                        navController.navigate(Screen.Signup.name)
                    },
                )
            }
        }

        composable(Screen.Signup.name) {
            ScreenWrapper(
                title = Screen.Signup.title,
                navigationAction = {
                    navController.navigateUp()
                },
            ) {
                Text(text = "Signup")
            }

        }
    }
}

enum class Screen(val title: String) {
    Login(title = "Login"),
    Signup(title = "Signup")
}