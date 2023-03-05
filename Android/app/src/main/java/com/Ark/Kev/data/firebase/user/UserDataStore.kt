package com.Ark.Kev.data.firebase.user

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.Ark.Kev.data.firebase.user.model.User

class UserDataStore {

    private val auth = Firebase.auth
    private val database = Firebase.database
    private val userId = auth.currentUser!!.uid

    fun get(
        onSuccess:(User) -> Unit
    ){
        database.reference.child("users")
            .child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.getValue<User>()!!)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun updateCountAds(count: Int) {
        database.reference.child("users")
            .child(userId)
            .child("countAds")
            .setValue(count)
    }

    fun updateCountAdsClick(count: Int){
        database.reference.child("users")
            .child(userId)
            .child("countAdsClick")
            .setValue(count)
    }

    fun updateCountAnswers(count: Int) {
        database.reference.child("users")
            .child(userId)
            .child("countAnswers")
            .setValue(count)
    }

    fun updateCountClickWatchAds(count: Int){
        database.reference.child("users")
            .child(userId)
            .child("countClickWatchAds")
            .setValue(count)
    }
}