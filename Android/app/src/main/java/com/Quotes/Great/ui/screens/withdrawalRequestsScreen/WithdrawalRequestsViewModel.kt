package com.Quotes.Great.ui.screens.withdrawalRequestsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.Quotes.Great.data.firebase.withdrawalRequest.WithdrawalRequestDataStore
import com.Quotes.Great.data.firebase.withdrawalRequest.model.WithdrawalRequest
import com.Quotes.Great.data.firebase.withdrawalRequest.model.WithdrawalRequestStatus

class WithdrawalRequestsViewModel(
    private val withdrawalRequestDataStore: WithdrawalRequestDataStore = WithdrawalRequestDataStore()
): ViewModel() {

    fun getWithdrawalRequests(onSuccess: (List<WithdrawalRequest>) -> Unit){
        withdrawalRequestDataStore.getAll(onSuccess) { message ->
            Log.e("getWithdrawalRequests",message)
        }
    }

    fun updateWithdrawalRequestStatus(
        id: String,
        status: WithdrawalRequestStatus,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    ){
        withdrawalRequestDataStore.updateStatus(id, status, onSuccess, onError)
    }
}