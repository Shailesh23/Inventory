package com.shal.inventory.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import com.shal.inventory.ui.theme.InventoryTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
                    floatingActionButton = {
                        FloatingActionButton(backgroundColor = colorResource(id = R.color.primary_color),
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
                                //hide bottom before pushing items
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }

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
        inventoryItem: InventoryItem, addItemListener: (InventoryItem) -> Unit
    ) {
        var name by remember { mutableStateOf(inventoryItem.name ?: "") }
        var description by remember { mutableStateOf(inventoryItem.description ?: "") }
        var qunatity by remember { mutableStateOf(inventoryItem.quantity ?: "") }
        val id = inventoryItem.id

        Text(
            text = if (inventoryItem.id.isNullOrEmpty()) "Add Inventory :" else "Update Inventory :",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 0.dp)
        )

        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(value = name,
                onValueChange = {name = it},
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Thoughts?") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = qunatity,
                onValueChange = { qunatity = it },
                label = { Text("How much?") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { addItemListener(InventoryItem(id, name, qunatity, description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp)
            ) {
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