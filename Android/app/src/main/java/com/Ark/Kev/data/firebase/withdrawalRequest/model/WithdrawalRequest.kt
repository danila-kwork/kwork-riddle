package com.Ark.Kev.data.firebase.withdrawalRequest.model

enum class WithdrawalRequestStatus(val text: String) {
    WAITING("Ожидает оплаты"),
    PAID(text = "Счет оплачен")
}

data class WithdrawalRequest(
    var id:String = "",
    var userId:String = "",
    val userEmail:String = "",
    val phoneNumber:String = "",
    val countAds:Int = 0,
    val countAdsClick:Int = 0,
    val countAnswers:Int = 0,
    val countClickWatchAds:Int = 0,
    val status: WithdrawalRequestStatus? = WithdrawalRequestStatus.WAITING
){
    fun dataMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String,Any>()

        map["id"] = id
        map["userId"] = userId
        map["userEmail"] = userEmail
        map["phoneNumber"] = phoneNumber
        map["countAds"] = countAds
        map["countAdsClick"] = countAdsClick
        map["countAnswers"] = countAnswers
        map["countClickWatchAds"] = countClickWatchAds
        status?.let { map["status"] = status }

        return map
    }
}