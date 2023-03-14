package com.shal.inventory.ui.theme

import androidx.compose.animation.core.Spring.DampingRatioHighBouncy
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shal.inventory.R
import com.shal.inventory.home.getSampleData
import com.shal.inventory.model.InventoryItem

@Composable
fun ToolBar(title: String, action : Pair<(ImageVector)->Unit, ImageVector>) {
    TopAppBar(title = { Text(title) },
        actions = {
            //create action with name and click listener
            IconButton(onClick = { action.first.invoke(action.second) }) {
                Icon(Icons.Default.Refresh, "action bar refresh icon")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateInventoryItem(inventoryList: List<InventoryItem>, listener: (InventoryItem) -> Unit) {
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) { scrollState.animateScrollTo(100) }
/*
    var list by remember { mutableStateOf(listOf("A", "B", "C")) }
    LazyColumn {
        item {
            Button(onClick = { list = list.shuffled() }) {
                Text("Shuffle")
            }
        }
        items(list, key = { it }) {
            Text("Item $it", Modifier.animateItemPlacement(
                spring(dampingRatio = DampingRatioHighBouncy)
            ))
        }
    }*/

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(inventoryList, key = {
            println("using ids : 4=${it.id}")
            it.id }) { inventoryItem ->
            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(
                        spring(dampingRatio = DampingRatioHighBouncy)
                    ),
                backgroundColor = PRIMARY_COLOR,
                shape = RoundedCornerShape(5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    CreateInventoryItem(inventoryItem, listener)
                }
            }
        }
    }
}

@Composable
private fun CreateInventoryItem(item: InventoryItem, listener: (InventoryItem) -> Unit) {
    Row(modifier = Modifier.clickable {
        listener(item)
    }) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 10.dp)
                .wrapContentWidth(Alignment.Start),
            text = item.name ?: "",
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(top = 0.dp, end = 0.dp)
                .wrapContentWidth(Alignment.End),
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            text = item.quantity ?: "",
            color = Color.White
        )
    }

    item.description?.let { description ->
        Text(
            modifier = Modifier, text = description
        )
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL)
@Composable
fun DefaultPreview() {
    InventoryTheme {
        CreateInventoryItem(getSampleData()) {

        }
    }
}