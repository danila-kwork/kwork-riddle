package com.Ark.Kev.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest

private const val AD_UNIT_ID = "R-M-2133372-2"

@Composable
fun YandexAdsBanner(
    modifier: Modifier = Modifier,
    size: AdSize = AdSize.BANNER_320x50 // BANNER_728x90
) {
    AndroidView(
        modifier = modifier,
        factory = {
            BannerAdView(it).apply {
                setAdUnitId(AD_UNIT_ID)
                setAdSize(size)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}