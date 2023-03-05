package com.Ark.Kev.data.firebase.priceAd

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class PriceAd(
    var countAds:Double = 0.0,
    var countAnswers:Double = 0.0,
    var countAdsClick:Double = 0.0,
    var countClickWatchAds:Double = 0.0
)

fun DataSnapshot.mapPriceAd(): PriceAd {

    val countAds = this.child("countAds").value.toString().toDouble()
    val countAnswers = this.child("countAnswers").value.toString().toDouble()
    val countAdsClick = this.child("countAdsClick").value.toString().toDouble()
    val countClickWatchAds = this.child("countClickWatchAds").value.toString().toDouble()

    return PriceAd(
        countAds = countAds,
        countAnswers = countAnswers,
        countAdsClick = countAdsClick,
        countClickWatchAds = countClickWatchAds
    )
}

class PriceAdDataStore {

    private val database = Firebase.database

    fun get(
        onSuccess:(PriceAd) -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ) {
        database.reference.child("ad_price").get()
            .addOnSuccessListener {
                onSuccess(it.mapPriceAd())
            }
            .addOnFailureListener { onFailure(it.message ?: "ошибка") }
    }

    fun editPrice(
        priceAd: PriceAd,
        onSuccess:() -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ) {
        database.reference.child("ad_price").setValue(priceAd)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "ошибка") }
    }
}