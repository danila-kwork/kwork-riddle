package com.Quotes.Great.data.firebase.withdrawalRequest.model

data class WithdrawalRequest(
    var id:String = "",
    var userId:String = "",
    val userEmail:String = "",
    val phoneNumber:String = "",
    val countAds:Int = 0,
    val countAdsClick:Int = 0,
    val countAnswers:Int = 0,
    val countClickWatchAds:Int = 0,
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

        return map
    }
}