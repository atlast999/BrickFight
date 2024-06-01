package presentation.authentication.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RegisterUI(
    email: String,
    username: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = {
                Text(
                    text = "Email"
                )
            }
        )
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
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onRegisterClicked,
        ) {
            Text(
                text = "Register"
            )
        }
    }
}