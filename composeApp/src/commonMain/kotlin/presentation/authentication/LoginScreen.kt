package presentation.authentication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LoginUI(
    username: String,
    onUsernameChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val curLoginClicked by remember {
            mutableStateOf(onLoginClicked)
        }

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChanged,
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
        )
        if (username.length > 5) {
            Text(
                "Enable login this is a f*cking long text that cannot fit into a single screen Enable login this is a f*cking long text that cannot fit into a single screen ",
                maxLines = 2,
                modifier = Modifier.basicMarquee()
            )
        }
        OutlinedButton(
            onClick = curLoginClicked,
        ) {
            Text(
                text = "Navigate"
            )
        }
    }
}