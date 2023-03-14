package com.shal.inventory.model

data class InventoryItem(
    val id: String = "",
    var name: String ? = null,
    var quantity: String ? = null,
    var description: String? = null,
    //todo add flag to buy items
    val creationTime: Long = System.currentTimeMillis()
)
