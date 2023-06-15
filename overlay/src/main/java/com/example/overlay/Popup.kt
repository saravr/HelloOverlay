package com.example.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PopUp(moveClicked: () -> Unit, clearClicked: () -> Unit) {
    Column(modifier = Modifier
        .background(Color.Transparent.copy(alpha = 0.2f))
        .fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = {
                moveClicked()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Move around", tint = Color.White)
            }
            IconButton(onClick = { clearClicked() }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
            }
        }
        Text("Hello", modifier = Modifier
            .padding(40.dp)
            .background(Color.Yellow))
    }
}
