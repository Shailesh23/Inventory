package com.shal.inventory.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import com.shal.inventory.ui.theme.InventoryTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shal.inventory.model.InventoryItem
import com.shal.inventory.ui.theme.CreateInventoryList
import com.shal.inventory.ui.theme.ToolBar
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import com.shal.inventory.R

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                val inventoryData = this.homeViewModel.inventoryFeedData.observeAsState()
                val editInventoryItem by homeViewModel.editInventoryItem.observeAsState()
                val bottomSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden
                )
                val coroutineScope = rememberCoroutineScope()

                ModalBottomSheetLayout(
                    sheetState = bottomSheetState,
                    sheetContent = {
                        val name = remember { mutableStateOf("") }
                        val description = remember { mutableStateOf("") }
                        val quantity = remember { mutableStateOf("") }
                        if (editInventoryItem != null) {
                            name.value = editInventoryItem?.name ?: ""
                            description.value = editInventoryItem?.description ?: ""
                            quantity.value = editInventoryItem?.quantity ?: ""
                            CreateBottomSheetContent(
                                editInventoryItem,
                                name,
                                description,
                                quantity
                            ) { item, isAddItem ->
                                //hide bottom before pushing items
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                                if(isAddItem) {
                                    homeViewModel.pushInventoryItem(item)
                                } else {
                                    homeViewModel.updateInventoryItem(item)
                                }
                            }
                        } else {
                            //need to show some anchor view for bottomsheet otherwise there will be an exception
                            Text(text = "")
                        }
                    }
                ) {
                    CreateScreenContentWithItemList(inventoryData, fabClickHandler = {
                        coroutineScope.launch {
                            if (bottomSheetState.isVisible.not()) {
                                homeViewModel.editInventoryItem.value = InventoryItem()
                                bottomSheetState.show()
                            } else {
                                bottomSheetState.hide()
                            }
                        }
                    }, {
                        homeViewModel.getInventoryItems()
                    }) { editItem ->
                        homeViewModel.editInventoryItem.value = editItem
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    }
                }
            }
        }
        homeViewModel.getInventoryItems()
    }

    @Composable
    private fun CreateScreenContentWithItemList(
        inventoryData: State<List<InventoryItem>?>?,
        fabClickHandler: () -> Unit,
        actionButtonListener: (ImageVector) -> Unit,
        itemSelectedListener: (InventoryItem) -> Unit
    ) {
        Scaffold(
            floatingActionButton = {
                CreateFloatingButtonForBottomSheet(fabClickHandler)
            },
            floatingActionButtonPosition = FabPosition.End,
        ) {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.padding(paddingValues = it)
            ) {
                Column {
                    ToolBar("Inventory", Pair(actionButtonListener, Icons.Default.Refresh))
                    inventoryData?.value?.let { itemList ->
                        CreateInventoryList(itemList, itemSelectedListener)
                    }
                }
            }
        }
    }

    @Composable
    private fun CreateBottomSheetContent(
        inventoryItemInBottomSheet: InventoryItem?,
        name: MutableState<String>,
        description: MutableState<String>,
        quantity: MutableState<String>,
        addItemListener: (InventoryItem, Boolean) -> Unit
    ) {
        inventoryItemInBottomSheet?.let { item ->
            Log.i("Bottomsheet", "showing ${item.name} with ID ${item.id}")
            CreateBottomSheetWithItemForm(item, name, description, quantity, addItemListener)
            item.id.ifEmpty { UUID.randomUUID() }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun CreateFloatingButtonForBottomSheet(
        fabClickHandler : () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        FloatingActionButton(backgroundColor = colorResource(id = R.color.primary_color),
            onClick = fabClickHandler) {
            Icon(Icons.Default.Add, "open bottomsheet")
        }
    }

    @Composable
    private fun CreateBottomSheetWithItemForm(
        inventoryItem: InventoryItem,
        name: MutableState<String>,
        description: MutableState<String>,
        quantity: MutableState<String>,
        addItemListener: (InventoryItem, Boolean) -> Unit
    ) {
        val id = inventoryItem.id
        Log.i("Bottomsheet", "setting ${inventoryItem.name} in bottom sheet")
        val isAddItem = inventoryItem.id.isEmpty()
        Text(
            text = if (isAddItem) "Add Inventory :" else "Update Inventory :",
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

            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Thoughts?") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantity.value,
                onValueChange = { quantity.value = it },
                label = { Text("How much?") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    addItemListener(
                        InventoryItem(
                            id,
                            name.value,
                            quantity.value,
                            description.value
                        ), isAddItem
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp, bottom = 0.dp),
                    text = if (isAddItem) "Add Item" else "Update Item",
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
            "",
            "Toor Dal",
            "1kg",
            description = "we got this item last month so need to check if it has expired"
        )
    )
    add(InventoryItem("", "Chana Dal", "1kg"))
}