package com.shal.inventory.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun ToolBar(title: String) {
    TopAppBar(title = { Text(title) })
}

@Composable
fun CreateInventoryItem(inventoryList: List<InventoryItem>, listener: (InventoryItem) -> Unit) {
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) { scrollState.animateScrollTo(100) }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(inventoryList) { inventoryItem ->
            CreateInventoryItem(inventoryItem, listener)
        }
    }
}

@Composable
private fun CreateInventoryItem(item: InventoryItem, listener: (InventoryItem) -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = PRIMARY_COLOR,
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.clickable {
                    listener(item)
                }
            ) {
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
                    modifier = Modifier,
                    text = description
                )
            }
        }
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