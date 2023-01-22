package com.shal.inventory.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.shal.inventory.model.InventoryItem

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepo()
    var inventoryFeedData: MutableLiveData<List<InventoryItem>>? = MutableLiveData()
    var editInventoryItem : MutableLiveData<InventoryItem> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        inventoryFeedData = null
    }

    fun pushInventoryItem(inventoryItem: InventoryItem) {
        firebaseRepo.addInventoryItem(inventoryItem)
    }

    fun getInventoryItems() {
        firebaseRepo.getInventoryList({ inventoryItems ->
            inventoryFeedData?.value = inventoryItems
        }) {
            //show error todo
        }
    }
}