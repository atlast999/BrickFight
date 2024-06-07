package presentation.room

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.ChatMessage
import domain.Room
import domain.isIncoming
import io.github.aakira.napier.Napier

@Composable
fun RoomUI(
    room: Room,
    messages: SnapshotStateList<ChatMessage>,
    onMessageSendClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Room: ${room.name}")
        Text(text = "Members: ")
        room.members.forEach { member ->
            Text(text = "${member.name} : ${member.id}")
        }
        Napier.d("messages: ${messages.joinToString { it.content }}")
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(messages) { message ->
                if (message.isIncoming) {
                    IncomingMessageItem(
                        sender = message.sender?.name!!,
                        content = message.content
                    )
                } else {
                    OutgoingMessageItem(content = message.content)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        var text by remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = text,
                onValueChange = { text = it },
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = { onMessageSendClicked.invoke(text) }
            ) {
                Text(text = "Send")
            }
        }
    }

}

@Composable
private fun OutgoingMessageItem(
    modifier: Modifier = Modifier,
    content: String,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
            ),
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = content)
            }
        }
    }
}

@Composable
private fun IncomingMessageItem(
    modifier: Modifier = Modifier,
    sender: String,
    content: String,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp),
    ) {
        Text(
            text = sender,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
        )
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 16.dp,
            ),
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = content)
            }
        }
    }
}