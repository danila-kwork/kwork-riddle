package com.Ark.Kev.data.firebase.questions

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.Ark.Kev.data.firebase.questions.model.Question
import com.Ark.Kev.data.firebase.questions.model.mapQuestion
import kotlin.random.Random

class QuestionDataStore {

    private val database = Firebase.database

    fun getRandom(
        onSuccess:(Question) -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ){
        val questionId = Random.nextInt(0,148).toString()

        database.reference.child("questions").child(questionId).get()
            .addOnSuccessListener { onSuccess(it.mapQuestion()) }
            .addOnFailureListener { onFailure(it.message ?: "ошибка") }
    }
}