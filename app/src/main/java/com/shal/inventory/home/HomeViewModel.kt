package com.shal.inventory.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.shal.inventory.model.InventoryItem

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseRepo()
    var inventoryFeedData: MutableLiveData<ArrayList<InventoryItem>> = MutableLiveData()
    var editInventoryItem : MutableLiveData<InventoryItem> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        inventoryFeedData.value?.clear()
    }

    fun pushInventoryItem(inventoryItem: InventoryItem) {
        firebaseRepo.addInventoryItem(inventoryItem)
        val existingList = ArrayList(inventoryFeedData.value as ArrayList)

        //always add new item to the top of the list
        existingList.add(0, inventoryItem).also {
            inventoryFeedData.value = existingList
        }
    }

    fun getInventoryItems() {
        firebaseRepo.getInventoryList({ inventoryItems ->
            inventoryItems.sortByDescending { it.creationTime }
            inventoryFeedData.value = inventoryItems
        }) {
            //show error todo
        }
    }
}