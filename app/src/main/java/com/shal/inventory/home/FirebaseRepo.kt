package com.shal.inventory.home

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shal.inventory.model.InventoryItem

class FirebaseRepo {
    private val db = Firebase.firestore

    companion object {
        const val collectionName = "inventoryCollection"
        const val TAG = "FirebaseRepo"
        const val NAME = "name"
        const val QUANTITY = "quantity"
        const val DESCRIPTION = "description"
    }

    fun addInventoryItem(inventoryItem: InventoryItem) {
        // Create a new user with a first and last name
        val user = hashMapOf(
            NAME to inventoryItem.name,
            QUANTITY to inventoryItem.quantity
        )

        // Add a new document with a generated ID
        db.collection(collectionName)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getInventoryList(
        successHandler: (List<InventoryItem>) -> Unit,
        failureHandler: () -> Unit
    ) {
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                val inventoryList = arrayListOf<InventoryItem>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    inventoryList.add(
                        InventoryItem(
                            id =  document.id,
                            name = document.data[NAME] as String,
                            quantity = document.data[QUANTITY] as String,
                            description = document.data[DESCRIPTION] as String
                        )
                    )
                }
                successHandler(inventoryList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                failureHandler()
            }
    }
}