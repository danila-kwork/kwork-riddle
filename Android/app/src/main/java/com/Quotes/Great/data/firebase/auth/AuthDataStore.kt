package com.Quotes.Great.data.firebase.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.Quotes.Great.data.firebase.user.model.User

class AuthDataStore {

    private val auth = Firebase.auth
    private val database = Firebase.database

    fun signIn(email:String,password: String, onSuccess:() -> Unit, onError:(message:String) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }else{
                    onError(task.exception?.message ?: "Ошибка авторизации")
                }
            }
    }

    fun registration(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createUser(
                        id = auth.currentUser!!.uid,
                        email = email,
                        password = password
                    ){
                        if(it.isSuccessful){
                            onSuccess()
                        }else{
                            onError(it.exception?.message ?: "Ошибка авторизации")
                        }
                    }
                }else{
                    onError(task.exception?.message ?: "Ошибка авторизации")
                }
            }
    }

    private fun createUser(
        id: String,
        email: String,
        password: String,
        onCompleteListener: (Task<Void>) -> Unit = {},
    ){
        val user = User(id = id,email = email, password = password)

        database.reference.child("users").child(id).updateChildren(user.dataMap())
            .addOnCompleteListener {
                onCompleteListener(it)
            }
    }
}