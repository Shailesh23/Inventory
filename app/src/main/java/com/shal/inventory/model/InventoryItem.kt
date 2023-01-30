package com.shal.inventory.model

data class InventoryItem(
    val id: String? = null,
    var name: String ? = null,
    var quantity: String ? = null,
    var description: String? = null,
    val creationTime: Long = System.currentTimeMillis()
)
