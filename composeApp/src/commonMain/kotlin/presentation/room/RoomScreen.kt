package presentation.room

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.Room

@Composable
fun RoomUI(
    room: Room,
    message: String,
    onMessageSendClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Room: ${room.name}")
        Text(text = "Members: ")
        room.members.forEach { member ->
            Text(text = member.name)
        }
        Text(text = "Last message: $message")
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
        )
        OutlinedButton(
            onClick = { onMessageSendClicked.invoke(text) }
        ) {
            Text(text = "Send")
        }
    }

}