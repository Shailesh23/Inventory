package com.shal.inventory.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.shal.inventory.ui.theme.InventoryTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shal.inventory.R
import com.shal.inventory.model.InventoryItem
import com.shal.inventory.ui.theme.CreateInventoryItem
import com.shal.inventory.ui.theme.ToolBar
import kotlinx.coroutines.launch
import java.util.UUID

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                val inventoryData = this.homeViewModel.inventoryFeedData?.observeAsState()
                val editInventoryItem = homeViewModel.editInventoryItem.observeAsState()
                val coroutineScope = rememberCoroutineScope()
                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberBottomSheetState(
                        initialValue = BottomSheetValue.Collapsed
                    )
                )
                BottomSheetScaffold(scaffoldState = bottomSheetScaffoldState,
                    floatingActionButton =
                    {
                        FloatingActionButton(
                            backgroundColor = colorResource(id = R.color.primary_color),
                            onClick = {
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            }) {
                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                Icon(Icons.Default.KeyboardArrowUp, "open bottomsheet")
                            } else {
                                Icon(Icons.Default.KeyboardArrowDown, "close bottomsheet")
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    sheetContent = {
                        val inventoryItemInBottomSheet = if (editInventoryItem.value != null) {
                            editInventoryItem.value
                        } else {
                            InventoryItem()
                        }
                        inventoryItemInBottomSheet?.let { item ->
                            CreateBottomSheetWithItemForm(item) {
                                homeViewModel.pushInventoryItem(it)
                            }
                            item.id?.ifEmpty { UUID.randomUUID() }
                        }
                    }) {
                    Surface(color = MaterialTheme.colors.background) {
                        Column {
                            ToolBar("Inventory")
                            inventoryData?.value?.let {
                                CreateInventoryItem(it)
                            }
                        }
                    }
                }
            }
        }
        homeViewModel.getInventoryItems()
    }

    @Composable
    private fun CreateBottomSheetWithItemForm(
        inventoryItem: InventoryItem,
        addItemListener: (InventoryItem) -> Unit
    ) {

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (inventoryItem.id.isNullOrEmpty()) "Create new item:" else "Update item:",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = inventoryItem.name ?: "",
                onValueChange = { inventoryItem.name = it },
                label = { Text("Item") }
            )

            OutlinedTextField(
                value = inventoryItem.description ?: "",
                onValueChange = { inventoryItem.description = it },
                label = { Text("Thoughts?") }
            )

            OutlinedTextField(
                value = inventoryItem.quantity ?: "",
                onValueChange = { inventoryItem.quantity = it },
                label = { Text("How much?") }

            )

            Button(onClick = {}, modifier = Modifier.width(40.dp)) {
                Text(
                    modifier = Modifier.padding(10.dp, bottom = 0.dp),
                    text = if (inventoryItem.id.isNullOrEmpty()) "Add Item" else "Update Item",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/*
@Preview(showSystemUi = true, device = Devices.PIXEL)
@Composable
fun DefaultPreview() {
    InventoryTheme {
        CreateInventoryItem(getSampleData())
    }
}*/

fun getSampleData() = arrayListOf<InventoryItem>().apply {
    add(
        InventoryItem(
            null,
            "Toor Dal",
            "1kg",
            description = "we got this item last month so need to check if it has expired"
        )
    )
    add(InventoryItem(null, "Chana Dal", "1kg"))
}