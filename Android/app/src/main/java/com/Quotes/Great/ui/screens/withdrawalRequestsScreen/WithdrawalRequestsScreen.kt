package com.Quotes.Great.ui.screens.withdrawalRequestsScreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.Quotes.Great.common.setClipboard
import com.Quotes.Great.data.firebase.priceAd.PriceAd
import com.Quotes.Great.data.firebase.priceAd.PriceAdDataStore
import com.Quotes.Great.data.firebase.user.model.userSumMoney
import com.Quotes.Great.data.firebase.withdrawalRequest.model.WithdrawalRequest
import com.Quotes.Great.data.firebase.withdrawalRequest.model.WithdrawalRequestStatus
import com.Quotes.Great.ui.theme.primaryBackground
import com.Quotes.Great.ui.theme.primaryText
import com.Quotes.Great.ui.theme.secondaryBackground
import com.Quotes.Great.ui.theme.tintColor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WithdrawalRequestsScreen(
    viewModel: WithdrawalRequestsViewModel = viewModel(),
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var message by remember { mutableStateOf("") }

    var withdrawalRequests by remember { mutableStateOf(listOf<WithdrawalRequest>()) }
    var deleteWithdrawalRequestId by remember { mutableStateOf("") }

    var isRefreshing by remember { mutableStateOf(false) }
    val priceAdDataStore = remember(::PriceAdDataStore)
    var priceAd by remember { mutableStateOf<PriceAd?>(null) }
    var editPriceDialog by remember { mutableStateOf(false) }
    var withdrawalRequestStatus by remember { mutableStateOf(WithdrawalRequestStatus.WAITING) }
    var withdrawalRequestStatusMenu by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        isRefreshing = true

        viewModel.getWithdrawalRequests {
            scope.launch {
                withdrawalRequests = it

                if(it.isEmpty())
                    message = "Пусто"

                delay(500L)

                isRefreshing = false
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {

        priceAdDataStore.get(
            onSuccess = {
                priceAd = it
            }
        )
    })

    LaunchedEffect(key1 = withdrawalRequestStatus, block = {
        viewModel.getWithdrawalRequests {
            withdrawalRequests = it.filter {
                if(it.status == null)
                    return@filter true
                else
                    it.status == withdrawalRequestStatus
            }

            if(it.isEmpty())
                message = "Пусто"
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = secondaryBackground,
                title = {
                    Column {
                        TextButton(onClick = { withdrawalRequestStatusMenu = true }) {
                            Text(text = withdrawalRequestStatus.text, color = tintColor)
                        }

                        DropdownMenu(
                            expanded = withdrawalRequestStatusMenu,
                            onDismissRequest = { withdrawalRequestStatusMenu = false },
                            modifier = Modifier.background(primaryBackground)
                        ) {
                            WithdrawalRequestStatus.values().forEach {
                                DropdownMenuItem(
                                    modifier = Modifier.background(primaryBackground),
                                    onClick = {
                                        withdrawalRequestStatus  = it
                                        withdrawalRequestStatusMenu = false
                                    }) {
                                        Text(
                                            text = it.text,
                                            color = if(withdrawalRequestStatus == it)
                                                tintColor
                                            else
                                                primaryText
                                        )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { editPriceDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = tintColor
                        )
                    }
                }
            )
        }
    ) {
        if(deleteWithdrawalRequestId.isNotEmpty()){
            DeleteWithdrawalRequestAlertDialog(
                onDismissRequest = {
                    deleteWithdrawalRequestId = ""
                },
                confirm = {
                    viewModel.updateWithdrawalRequestStatus(
                        id = deleteWithdrawalRequestId,
                        status = when(withdrawalRequestStatus){
                            WithdrawalRequestStatus.WAITING -> WithdrawalRequestStatus.PAID
                            WithdrawalRequestStatus.PAID -> WithdrawalRequestStatus.WAITING
                        },
                        onSuccess = {
                            withdrawalRequests = emptyList()

                            viewModel.getWithdrawalRequests {
                                withdrawalRequests = it.filter { it.status == withdrawalRequestStatus }

                                if(it.isEmpty())
                                    message = "Пусто"
                            }

                            deleteWithdrawalRequestId = ""
                        },
                        onError = {
                            Toast.makeText(context, "Ошибка: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        if(editPriceDialog){
            EditPriceDialog(
                priceAd = priceAd ?: PriceAd(),
                onDismissRequest = { editPriceDialog = false },
                editPrice = {
                    priceAdDataStore.editPrice(
                        priceAd = it,
                        onSuccess = {
                            editPriceDialog = false

                            priceAdDataStore.get(
                                onSuccess = {
                                    priceAd = it
                                }
                            )
                        }
                    )
                }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState),
            color = primaryBackground
        ) {
            Box(Modifier.pullRefresh(pullRefreshState)) {

                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item {
                        AnimatedVisibility(visible = message.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = message,
                                    color = Color.Red,
                                    fontWeight = FontWeight.W900,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }

                    items(withdrawalRequests){ item ->
                        Text(
                            text = "Индификатор пользователя : ${item.userId}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.userId)
                                    })
                                }
                        )

                        Text(
                            text = "Электронная почта : ${item.userEmail}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.userEmail)
                                    })
                                }
                        )

                        Text(
                            text = "Номер телефона : ${item.phoneNumber}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.phoneNumber)
                                    })
                                }
                        )

                        Text(
                            text = "Количество просмотренной рекламы : ${item.countAds}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.countAds.toString())
                                    })
                                }
                        )

                        Text(
                            text = "Сколько раз пользователь перешел на сайт : ${item.countAdsClick}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.countAds.toString())
                                    })
                                }
                        )

                        Text(
                            text = "Количество правельных ответов : ${item.countAnswers}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.countAds.toString())
                                    })
                                }
                        )

                        Text(
                            text = "Количество просмотреной рекламы, конпка Смотреть: ${item.countAdsClick}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(context, item.countAdsClick.toString())
                                    })
                                }
                        )

                        Text(
                            text = if(priceAd == null)
                                ""
                            else
                                "Сумма для ввывода : ${userSumMoney(
                                    priceAd!!,
                                    item.countAds,
                                    item.countAnswers,
                                    item.countAdsClick,
                                    item.countClickWatchAds
                                )}",
                            color = primaryText,
                            modifier = Modifier
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onLongPress = {
                                        setClipboard(
                                            context,
                                            if (priceAd == null)
                                                ""
                                            else
                                                "Сумма для ввывода : ${
                                                    userSumMoney(
                                                        priceAd!!,
                                                        item.countAds,
                                                        item.countAnswers,
                                                        item.countAdsClick,
                                                        item.countClickWatchAds
                                                    )
                                                }"
                                        )
                                    })
                                }
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            onClick = { deleteWithdrawalRequestId = item.id },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = tintColor
                            )
                        ) {
                            Text(
                                text = "Изменить статус на '" +
                                        "${when(item.status){
                                            WithdrawalRequestStatus.WAITING -> "Счет оплачен"
                                            WithdrawalRequestStatus.PAID -> "Ожидает оплаты"
                                            null -> "Счет оплачен"
                                        }}'",
                                color = primaryText,
                                textAlign = TextAlign.Center
                            )
                        }

                        Divider(color = tintColor)
                    }
                }

                PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
private fun DeleteWithdrawalRequestAlertDialog(
    onDismissRequest: () -> Unit,
    confirm: () -> Unit
) {
    AlertDialog(
        backgroundColor = primaryBackground,
        shape = AbsoluteRoundedCornerShape(15.dp),
        onDismissRequest = onDismissRequest,
        buttons = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                onClick = confirm
            ) {
                Text(text = "Подтвердить", color = Color.Red)
            }
        }
    )
}

@Composable
private fun EditPriceDialog(
    priceAd: PriceAd,
    onDismissRequest: () -> Unit,
    editPrice: (PriceAd) -> Unit
) {
    var countAds by remember { mutableStateOf("") }
    var countAnswers by remember { mutableStateOf("") }
    var countAdsClick by remember { mutableStateOf("") }
    var countClickWatchAds by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit, block = {
        countAds = priceAd.countAds.toString()
        countAnswers = priceAd.countAnswers.toString()
        countAdsClick = priceAd.countAdsClick.toString()
        countClickWatchAds = priceAd.countClickWatchAds.toString()
    })

    AlertDialog(
        modifier = Modifier.height(450.dp),
        backgroundColor = primaryBackground,
        shape = AbsoluteRoundedCornerShape(15.dp),
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                OutlinedTextField(
                    value = countAds,
                    onValueChange = { countAds = it },
                    modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = {
                        Text(
                            text = "Видео с вознаграждением и полноэкранная (без перехода на сайт)",
                            color = primaryText
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = primaryText
                    )
                )

                OutlinedTextField(
                    value = countAnswers,
                    onValueChange = { countAnswers = it },
                    modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = {
                        Text(
                            text = "Правильный ответ (без подсказки)",
                            color = primaryText
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = primaryText
                    )
                )

                OutlinedTextField(
                    value = countAdsClick,
                    onValueChange = { countAdsClick = it },
                    modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = {
                        Text(
                            text = "Видео с вознаграждением (переходом на сайт)",
                            color = primaryText
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = primaryText
                    )
                )

                OutlinedTextField(
                    value = countClickWatchAds,
                    onValueChange = { countClickWatchAds = it },
                    modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = {
                        Text(
                            text = "Полноэкранная (переходом на сайт)",
                            color = primaryText
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = primaryText
                    )
                )
            }
        },
        buttons = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                onClick = {
                    editPrice(
                        PriceAd(
                            countAds = countAds.toDouble(),
                            countAnswers = countAnswers.toDouble(),
                            countAdsClick = countAdsClick.toDouble(),
                            countClickWatchAds = countClickWatchAds.toDouble()
                        )
                    )
                }
            ) {
                Text(text = "Сохранить", color = Color.Red)
            }
        }
    )
}