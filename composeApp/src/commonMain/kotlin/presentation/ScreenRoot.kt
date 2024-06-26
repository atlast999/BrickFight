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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import domain.ChatMessage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.authentication.login.LoginUI
import presentation.authentication.login.LoginViewModel
import presentation.authentication.register.RegisterUI
import presentation.authentication.register.RegisterViewModel
import presentation.room.call.CallUI
import presentation.room.call.CallViewModel
import presentation.room.chat.RoomUI
import presentation.room.chat.RoomViewModel
import presentation.room.list.ListRoomUI
import presentation.room.list.ListRoomViewModel
import presentation.room.list.NewRoomDialog

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

@OptIn(KoinExperimentalAPI::class)
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
            val viewModel = koinViewModel<LoginViewModel>()
            val uiState by viewModel.state.collectAsState()
            LaunchedEffect(true) {
                viewModel.flowNavHome.collectLatest {
                    navController.navigate(Screen.ListRooms.name)
                }
            }
            ScreenWrapper(
                isLoading = uiState.isLoading,
                title = Screen.Login.title,
                navigationIcon = {},
            ) {
                LoginUI(
                    username = uiState.username,
                    onUsernameChanged = viewModel::onUsernameChanged,
                    password = uiState.password,
                    onPasswordChanged = viewModel::onPasswordChanged,
                    onLoginClicked = viewModel::onLoginClick,
                    onRegisterClicked = {
//                        navController.navigate(Screen.Signup.name)
                        navController.navigate(Screen.Call.name)
                    }
                )
            }
        }

        composable(Screen.Signup.name) {
            val viewModel = koinViewModel<RegisterViewModel>()
            val uiState by viewModel.state.collectAsState()
            LaunchedEffect(true) {
                viewModel.flowNavHome.collectLatest {
                    navController.navigate(Screen.ListRooms.name)
                }
            }
            ScreenWrapper(
                isLoading = uiState.isLoading,
                title = Screen.Signup.title,
                navigationAction = {
                    navController.navigateUp()
                },
            ) {
                if (uiState.errorMessage == null) {
                    RegisterUI(
                        email = uiState.email,
                        username = uiState.username,
                        password = uiState.password,
                        onEmailChanged = viewModel::onEmailChanged,
                        onUsernameChanged = viewModel::onUsernameChanged,
                        onPasswordChanged = viewModel::onPasswordChanged,
                        onRegisterClicked = viewModel::onRegisterClick,
                    )
                } else {
                    Text(text = uiState.errorMessage!!)
                }
            }

        }

        composable(Screen.ListRooms.name) {
            val viewModel = koinViewModel<ListRoomViewModel>()
            val uiState by viewModel.state.collectAsState()
            LaunchedEffect(true) {
                viewModel.flowJoinedRoom.collectLatest {
                    navController.navigate("${Screen.Room.name}/$it")
                }
            }
            val openCreateDialog = remember { mutableStateOf(false) }
            ScreenWrapper(
                isLoading = uiState.isLoading,
                title = "Chat rooms",
                navigationAction = {
                    navController.navigateUp()
                },
                actions = {
                    IconButton(
                        onClick = {
                            openCreateDialog.value = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        onClick = viewModel::loadRooms,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                        )
                    }
                }
            ) {
                if (openCreateDialog.value) {
                    NewRoomDialog(
                        onDismissRequest = {
                            openCreateDialog.value = false
                        },
                        onCreateClicked = viewModel::createRoom,
                    )
                    return@ScreenWrapper
                }
                ListRoomUI(
                    rooms = uiState.rooms,
                    onRoomClicked = viewModel::joinRoom,
                )
            }
        }

        composable("${Screen.Room.name}/{roomId}") { backStackEntry ->
            val roomId =
                backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: return@composable
            val viewModel = koinViewModel<RoomViewModel>()
            val uiState by viewModel.state.collectAsState()
            LaunchedEffect(true) {
                launch {
                    viewModel.loadRoom(roomId = roomId)
                }
                launch {
                    viewModel.flowLeaveRoom.collectLatest {
                        navController.navigateUp()
                    }
                }
            }
            val listMessage = remember { mutableStateListOf<ChatMessage>() }

            LaunchedEffect(uiState.incomingMessage) {
                uiState.incomingMessage?.let {
                    listMessage.add(it)
                }
            }
            ScreenWrapper(
                isLoading = uiState.isLoading,
                title = Screen.Room.title,
                navigationAction = {
                    viewModel.leaveRoom()
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Call.name)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                        )
                    }
                }
            ) {
                uiState.room?.let {
                    RoomUI(
                        room = it,
                        messages = listMessage,
                        outgoingMessage = uiState.outgoingMessage,
                        onOutgoingMessageChanged = viewModel::onOutgoingMessageChanged,
                        onMessageSendClicked = viewModel::sendMessage,
                    )
                }
            }
        }

        composable(Screen.Call.name) {
            val viewModel = koinViewModel<CallViewModel>()
            val incomingFrame by viewModel.frameData.collectAsState()
            DisposableEffect(true) {
                onDispose {
                    viewModel.stopStream()
                }
            }
            ScreenWrapper(
                title = Screen.Call.title,
                navigationAction = {
                    navController.navigateUp()
                },
            ) {
                CallUI(
                    incomingFrame = incomingFrame,
                    onFrameReceived = viewModel::streamImageByteArray,
                )
            }

        }

    }
}

enum class Screen(val title: String) {
    Login(title = "Login"),
    Signup(title = "Signup"),
    ListRooms(title = "ListRooms"),
    Room(title = "Room"),
    Call(title = "Call")
}