package com.Quotes.Great.yandexAds

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import org.joda.time.LocalDateTime

class RewardedYandexAds(
    private val context:Context,
    private val onDismissed:(
        adClickedDate: LocalDateTime?,
        returnedToDate: LocalDateTime?,
        rewarded:Boolean
    ) -> Unit
): RewardedAdEventListener {

    private var rewardedAd:RewardedAd? = RewardedAd(context)

    init { configureRewardedAd() }

    private var adClickedDate: LocalDateTime? = null
    private var returnedToDate: LocalDateTime? = null
    private var rewarded = false

    fun show() {
        adClickedDate = null
        returnedToDate = null
        rewarded = false

        loadRewardedAd()
    }

    private fun loadRewardedAd() = rewardedAd?.loadAd(AdRequest.Builder().build())

    private fun configureRewardedAd() {
        rewardedAd?.setAdUnitId(AD_UNIT_ID)
        rewardedAd?.setRewardedAdEventListener(this)
    }

    private companion object { const val AD_UNIT_ID = "R-M-2128637-8" }

    override fun onAdLoaded() { rewardedAd?.show() }

    override fun onAdFailedToLoad(p0: AdRequestError) {
        Toast.makeText(context, p0.code.toString() + p0.description, Toast.LENGTH_SHORT).show()
        Log.e("onAdFailedToLoad",p0.code.toString() + p0.description)
    }

    override fun onAdShown() = Unit

    override fun onAdDismissed() {
        onDismissed(adClickedDate,returnedToDate, rewarded)
    }

    override fun onRewarded(p0: Reward) {
        rewarded = true
    }

    override fun onAdClicked() {
        adClickedDate = LocalDateTime.now()
    }

    override fun onLeftApplication() = Unit

    override fun onReturnedToApplication() {
        returnedToDate = LocalDateTime.now()
    }

    override fun onImpression(p0: ImpressionData?) = Unit

    fun destroy() {
        rewardedAd?.destroy()
        rewardedAd = null
    }
}