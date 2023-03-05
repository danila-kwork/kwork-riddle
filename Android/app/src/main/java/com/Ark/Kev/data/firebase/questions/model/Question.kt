package com.Ark.Kev.data.firebase.questions.model

import com.google.firebase.database.DataSnapshot

data class Question(
    val question:String,
    val answer:String
)

fun questionLoading():Question {
    return Question(
        question = "Загрузка",
        answer = ""
    )
}

fun DataSnapshot.mapQuestion(): Question {

    val question = this.child("question").value.toString()
    val answer = this.child("answer").value.toString()

    return Question(
        question = question,
        answer = answer
    )
}