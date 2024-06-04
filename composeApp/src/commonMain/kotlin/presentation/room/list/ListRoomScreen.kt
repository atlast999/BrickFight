package presentation.room.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import domain.Room

@Composable
internal fun ListRoomUI(
    rooms: List<Room>,
    onRoomClicked: (Room) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(rooms) { room ->
            RoomItem(
                modifier = Modifier.fillMaxWidth(),
                room = room,
                onRoomClicked = onRoomClicked,
            )

        }
    }
}

@Composable
private fun RoomItem(
    modifier: Modifier,
    room: Room,
    onRoomClicked: (Room) -> Unit,
) {
    Card(
        onClick = { onRoomClicked.invoke(room) },
        modifier = modifier.padding(10.dp),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
                    .background(color = Color.Transparent, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = room.name
            )
        }
    }
}

@Composable
internal fun NewRoomDialog(
    onDismissRequest: () -> Unit,
    onCreateClicked: (String) -> Unit,
) {
    var name by remember {
        mutableStateOf("")
    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        Card(
            modifier = Modifier.height(200.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
            ) {
                Text(
                    text = "Enter room's name:"
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it }
                )
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onDismissRequest.invoke()
                        onCreateClicked.invoke(name)
                    }
                ) {
                    Text(
                        text = "Create"
                    )
                }
            }
        }
    }
}