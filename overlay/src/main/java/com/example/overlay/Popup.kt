package com.example.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun PopUp(data: Flow<List<String>>, moveClicked: () -> Unit, clearClicked: () -> Unit) {
    val dataItems by data.collectAsState(listOf())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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

        LazyColumn(state = listState) {
            coroutineScope.launch { // TODO!!!
                if (dataItems.size > 6) {
                    listState.scrollToItem(dataItems.size - 1)
                }
            }
            items(
                dataItems
            ) {
                Text(it, modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
                    .background(Color.Yellow), style = MaterialTheme.typography.h5)
                Divider()
            }
        }
    }
}
