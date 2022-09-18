package com.shal.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.shal.inventory.ui.theme.InventoryTheme
import androidx.compose.material.*
import com.shal.inventory.model.InventoryItem
import com.shal.inventory.ui.theme.CreateInventoryItem
import com.shal.inventory.ui.theme.ToolBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        ToolBar("Inventory")
                        CreateInventoryItem(getSampleData())
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL)
@Composable
fun DefaultPreview() {
    InventoryTheme {
        CreateInventoryItem(getSampleData())
    }
}

fun getSampleData() = arrayListOf<InventoryItem>().apply {
   add(InventoryItem("Toor Dal", R.drawable.item, "1kg"))
   add(InventoryItem("Chana Dal", R.drawable.item, "1kg"))
}