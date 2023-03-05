package com.Ark.Kev.ui.screens.mainScreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ark.Kev.LocalNavController
import com.Ark.Kev.R
import com.Ark.Kev.data.firebase.priceAd.PriceAd
import com.Ark.Kev.data.firebase.priceAd.PriceAdDataStore
import com.Ark.Kev.data.firebase.user.model.UserRole
import com.Ark.Kev.data.firebase.user.model.createUserLoading
import com.Ark.Kev.data.firebase.user.model.userSumMoney
import com.Ark.Kev.data.firebase.withdrawalRequest.model.WithdrawalRequest
import com.Ark.Kev.data.firebase.questions.model.questionLoading
import com.Ark.Kev.navigation.Screen
import com.Ark.Kev.ui.screens.mainScreen.view.RewardAlertDialog
import com.Ark.Kev.ui.view.OnLifecycleEvent
import com.Ark.Kev.ui.view.SchoolBoard
import com.Ark.Kev.ui.view.YandexAdsBanner
import com.Ark.Kev.yandexAds.InterstitialYandexAds
import com.Ark.Kev.yandexAds.RewardedYandexAds
import java.time.Duration

@SuppressLint("NewApi")
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current

    val navController = LocalNavController.current

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    var user by remember { mutableStateOf(createUserLoading()) }

    var answerAds by rememberSaveable { mutableStateOf(false) }
    var userAnswer by rememberSaveable { mutableStateOf("") }
    var clickCount by rememberSaveable { mutableStateOf(0) }
    var yandexAdsCount by rememberSaveable { mutableStateOf(0) }
    var question by remember { mutableStateOf(questionLoading()) }
    val priceAdDataStore = remember(::PriceAdDataStore)
    var priceAd by remember { mutableStateOf<PriceAd?>(null) }

    var rewardAlertDialog by remember { mutableStateOf(false) }

    var watchAdsClickButtonVisible by remember { mutableStateOf(true) }
    var watchAdsClick by remember { mutableStateOf(false) }

    val timer = object: CountDownTimer(7000, 1000) {
        override fun onTick(millisUntilFinished: Long) = Unit

        override fun onFinish() {
            watchAdsClickButtonVisible = true
        }
    }

    val rewardedYandexAds = remember {
        RewardedYandexAds(context, onDismissed = { adClickedDate, returnedToDate, rewarded ->
            if(adClickedDate != null && returnedToDate != null && rewarded){
                if((Duration.between(adClickedDate, returnedToDate)).seconds >= 10){
                    viewModel.updateCountAdsClick(user.countAdsClick + 1)
                }else {
                    viewModel.updateCountAds(user.countAds + 1)
                }
            } else if(rewarded){
                viewModel.updateCountAds(user.countAds + 1)
            }
        })
    }

    val interstitialYandexAds = remember {
        InterstitialYandexAds(context, onDismissed = { adClickedDate, returnedToDate ->
            if(watchAdsClick){
                watchAdsClickButtonVisible = false

                if(adClickedDate != null && returnedToDate != null){
                    if((Duration.between(adClickedDate, returnedToDate)).seconds >= 10){
                        viewModel.updateCountClickWatchAds(user.countClickWatchAds + 1)
                    }else {
                        viewModel.updateCountAds(user.countAds + 1)
                    }
                } else {
                    viewModel.updateCountAds(user.countAds + 1)
                }

                timer.start()
                watchAdsClick = false
            }
        })
    }

    OnLifecycleEvent { owner, event ->
        if(event == Lifecycle.Event.ON_DESTROY){
            rewardedYandexAds.destroy()
            interstitialYandexAds.destroy()
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        priceAdDataStore.get(
            onSuccess = { priceAd = it }
        )
        viewModel.getQuestionRandom(onSuccess = { question = it })
        viewModel.getUser { user = it }
    })

    LaunchedEffect(key1 = yandexAdsCount, block = {
        if(yandexAdsCount >= 3){
            rewardedYandexAds.show()
            yandexAdsCount = 0
        }
    })

    if(rewardAlertDialog){
        priceAd?.let {
            RewardAlertDialog(
                priceAd = it,
                countAds = user.countAds,
                countAnswers = user.countAnswers,
                countAdsClick = user.countAdsClick,
                countClickWatchAds = user.countClickWatchAds,
                onDismissRequest = {
                    rewardAlertDialog = false
                },
                onSendWithdrawalRequest = { phoneNumber ->
                    val withdrawalRequest = WithdrawalRequest(
                        countAds = user.countAds,
                        countAnswers = user.countAnswers,
                        countAdsClick = user.countAdsClick,
                        countClickWatchAds = user.countClickWatchAds,
                        phoneNumber = phoneNumber,
                        userEmail = user.email
                    )

                    viewModel.sendWithdrawalRequest(withdrawalRequest,{
                        rewardAlertDialog = false
                        Toast.makeText(context, "Заявка отправлена", Toast.LENGTH_SHORT).show()
                    },{ message ->
                        Toast.makeText(context, "Ошибка: $message", Toast.LENGTH_SHORT).show()
                    })
                }
            )
        }
    }

    Image(
        bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.main_background),
            screenWidthDp,
            screenHeightDp,
            false
        ).asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.size(
            width = screenWidthDp.dp,
            height = screenHeightDp.dp
        )
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {

//            Image(
//                bitmap = Bitmap.createScaledBitmap(
//                    BitmapFactory.decodeResource(context.resources,R.drawable.teacher),
//                    (screenWidthDp / 2),
//                    (screenHeightDp / 2),
//                    false
//                ).asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.size(
//                    width = (screenWidthDp / 2.0).dp,
//                    height = (screenHeightDp / 2.5).dp
//                )
//            )

            SchoolBoard(
                text = if(priceAd == null)
                    ""
                else
                    "Балланс ${userSumMoney(
                        priceAd!!,
                        user.countAds,
                        user.countAnswers,
                        user.countAdsClick,
                        user.countClickWatchAds
                    )}" + " ₽\n\n${question.question}",
                width = (screenWidthDp / 1.1),
                height = (screenHeightDp / 2.8)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height((screenHeightDp / 15).dp))

//            Text(
//                text = "Ответ:",
//                color = Color.Black,
//                fontStyle = FontStyle.Italic,
//                fontSize = 40.sp
//            )

            Box {
                Image(
                    bitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(context.resources,R.drawable.text_field),
                        (screenWidthDp / 2.4).toInt(),
                        (screenHeightDp / 13),
                        false
                    ).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            5.dp
                        )
                        .height((screenHeightDp / 13).dp)
                        .width((screenWidthDp / 2.4).dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(
                            5.dp
                        )
                        .height((screenHeightDp / 13).dp)
                        .width((screenWidthDp / 2.4).dp)

                ) {
                    TextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        placeholder = {
                            Text(text = "Ответ", color = Color.Yellow.copy(0.6f))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent,
                            unfocusedLabelColor = Color.Transparent,
                            focusedLabelColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height((screenHeightDp / 15).dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(2){ index ->
                    MainButton(
                        text = when (index) {
                            0 -> "Проверить"
                            1 -> "Подсказка"
                            else -> ""
                        }
                    ){
                        clickCount++

                        if (index == 1) {
                            answerAds = true
                            yandexAdsCount++
                            userAnswer = question.answer
                        } else if (index == 0) {
                            if (userAnswer
                                    .lowercase()
                                    .trim() == question.answer
                                    .lowercase()
                                    .trim()
                            ) {
                                if (!answerAds) {
                                    viewModel.updateCountAnswers(user.countAnswers + 1)
                                }

                                userAnswer = ""
                                answerAds = false
                                question = questionLoading()
                                viewModel.getQuestionRandom(onSuccess = {
                                    question = it
                                })
                                Toast
                                    .makeText(context,
                                        "Верно !",
                                        Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast
                                    .makeText(context,
                                        "Не верно",
                                        Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                MainButton(
                    text = "Награда"
                ){
                    rewardAlertDialog = true
                }

                AnimatedVisibility(visible = watchAdsClickButtonVisible) {
                    MainButton(
                        text = "Смотреть"
                    ){
                        watchAdsClick = true
                        interstitialYandexAds.show()
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(if(user.userRole == UserRole.ADMIN) 1 else 0){ index ->
                    MainButton(
                        text = when (index) {
                            0 -> "Админ"
                            else -> ""
                        }
                    ){
                        if (index == 0) {
                            navController.navigate(Screen.WithdrawalRequests.route)
                        }
                    }
                }
            }

            YandexAdsBanner()
        }
    }
}

@Composable
private fun MainButton(
    text:String,
    onClick: () -> Unit
) {

    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    Box {
        Image(
            bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.resources,R.drawable.main_button),
                (screenWidthDp / 2.4).toInt(),
                (screenHeightDp / 13),
                false
            ).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    5.dp
                )
                .height((screenHeightDp / 13).dp)
                .width((screenWidthDp / 2.4).dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(
                    5.dp
                )
                .height((screenHeightDp / 13).dp)
                .width((screenWidthDp / 2.4).dp)
                .clickable { onClick() }
        ) {
            Text(
                text = text,
                color = Color.Yellow,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )
        }

    }
}