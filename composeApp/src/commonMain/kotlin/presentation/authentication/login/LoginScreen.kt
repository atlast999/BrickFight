package presentation.authentication.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
internal fun LoginUI(
    username: String,
    onUsernameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChanged,
            label = {
                Text(
                    text = "Username"
                )
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = {
                Text(
                    text = "Password"
                )
            }
        )
        Text(
            text = "Register",
            modifier = Modifier.align(Alignment.End).clickable {
                onRegisterClicked()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            modifier = Modifier,
            onClick = onLoginClicked,
        ) {
            Text(
                text = "Login"
            )
        }
    }
}