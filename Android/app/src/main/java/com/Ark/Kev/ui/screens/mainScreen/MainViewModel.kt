package com.Ark.Kev.ui.screens.mainScreen

import androidx.lifecycle.ViewModel
import com.Ark.Kev.data.firebase.user.UserDataStore
import com.Ark.Kev.data.firebase.user.model.User
import com.Ark.Kev.data.firebase.withdrawalRequest.WithdrawalRequestDataStore
import com.Ark.Kev.data.firebase.withdrawalRequest.model.WithdrawalRequest
import com.Ark.Kev.data.firebase.questions.QuestionDataStore
import com.Ark.Kev.data.firebase.questions.model.Question

class MainViewModel(
    private val userDataStore: UserDataStore = UserDataStore(),
    private val wordsDataStore: QuestionDataStore = QuestionDataStore(),
    private val withdrawalRequestDataStore: WithdrawalRequestDataStore = WithdrawalRequestDataStore()
): ViewModel() {

    fun getQuestionRandom(
        onSuccess:(Question) -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ){
        wordsDataStore.getRandom(onSuccess, onFailure)
    }

    fun getUser(onSuccess:(User) -> Unit){
        userDataStore.get(onSuccess)
    }

    fun updateCountAds(count: Int){
        userDataStore.updateCountAds(count)
    }

    fun updateCountAdsClick(count: Int){
        userDataStore.updateCountAdsClick(count)
    }

    fun updateCountAnswers(count: Int){
        userDataStore.updateCountAnswers(count)
    }

    fun updateCountClickWatchAds(count: Int){
        userDataStore.updateCountClickWatchAds(count)
    }

    fun sendWithdrawalRequest(
        withdrawalRequest: WithdrawalRequest,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    ) {
        withdrawalRequestDataStore.create(
            withdrawalRequest = withdrawalRequest,
            onCompleteListener = {
                if(it.isSuccessful){
                    onSuccess()
                }else{
                    onError(it.exception?.message ?: "Ошибка")
                }
            }
        )
    }
}