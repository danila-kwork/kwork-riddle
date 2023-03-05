package com.Ark.Kev.yandexAds

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import java.time.LocalDateTime

class InterstitialYandexAds(
    private val context: Context,
    private val onDismissed: (
        adClickedDate: LocalDateTime?,
        returnedToDate: LocalDateTime?
    ) -> Unit
): InterstitialAdEventListener {

    private var interstitialAds: InterstitialAd? = InterstitialAd(context)

    private var adClickedDate: LocalDateTime? = null
    private var returnedToDate: LocalDateTime? = null

    init { configureRewardedAd() }

    private companion object {
        const val AD_UNIT_ID = "R-M-2133372-4"
    }

    fun show() {
        adClickedDate = null
        returnedToDate = null

        loadRewardedAd()
    }

    private fun loadRewardedAd() = interstitialAds?.loadAd(AdRequest.Builder().build())

    private fun configureRewardedAd() {
        interstitialAds?.setAdUnitId(AD_UNIT_ID)
        interstitialAds?.setInterstitialAdEventListener(this)
    }

    override fun onAdLoaded() { interstitialAds?.show() }

    override fun onAdFailedToLoad(p0: AdRequestError) {
        Toast.makeText(context, p0.code.toString() + p0.description, Toast.LENGTH_SHORT).show()
        Log.e("onAdFailedToLoad",p0.code.toString() + p0.description)
    }

    override fun onAdShown() = Unit

    override fun onAdDismissed() {
        onDismissed(adClickedDate,returnedToDate)
    }

    @SuppressLint("NewApi")
    override fun onAdClicked() {
        adClickedDate = LocalDateTime.now()
    }

    @SuppressLint("NewApi")
    override fun onLeftApplication() = Unit

    @SuppressLint("NewApi")
    override fun onReturnedToApplication() {
        returnedToDate = LocalDateTime.now()
    }

    override fun onImpression(p0: ImpressionData?) = Unit

    fun destroy() {
        interstitialAds?.destroy()
        interstitialAds = null
    }
}