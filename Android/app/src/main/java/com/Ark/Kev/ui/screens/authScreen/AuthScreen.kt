package com.Ark.Kev.ui.screens.authScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Ark.Kev.LocalNavController
import com.Ark.Kev.R
import com.Ark.Kev.navigation.Screen
import com.Ark.Kev.ui.theme.primaryText
import com.Ark.Kev.ui.theme.secondaryBackground
import com.Ark.Kev.ui.theme.tintColor
import com.Ark.Kev.ui.view.YandexAdsBanner
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.yandex.mobile.ads.banner.AdSize

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
) {
    val context = LocalContext.current

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    val navController = LocalNavController.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val crashlytics = remember(Firebase::crashlytics)

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

    LazyColumn(
        modifier = Modifier.size(
            width = screenWidthDp.dp,
            height = screenHeightDp.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            YandexAdsBanner(
                size = AdSize.BANNER_240x400
            )
        }

        item {

            AnimatedVisibility(visible = error.isNotEmpty()) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(5.dp)
                )
            }

            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                value = email,
                onValueChange = { email = it },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = primaryText,
                    disabledTextColor = tintColor,
                    backgroundColor = secondaryBackground,
                    cursorColor = tintColor,
                    focusedBorderColor = tintColor,
                    unfocusedBorderColor = secondaryBackground,
                    disabledBorderColor = secondaryBackground
                ),
                label = {
                    Text(text = "Электронная почта", color = primaryText)
                }
            )

            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = primaryText,
                    disabledTextColor = tintColor,
                    backgroundColor = secondaryBackground,
                    cursorColor = tintColor,
                    focusedBorderColor = tintColor,
                    unfocusedBorderColor = secondaryBackground,
                    disabledBorderColor = secondaryBackground
                ),
                label = {
                    Text(text = "Парроль", color = primaryText)
                }
            )

            MainButton(text = "Авторизироваться") {
                try {
                    error = ""

                    viewModel.signIn(email.trim(),password.trim(),{
                        navController.navigate(Screen.Main.route)
                    },{
                        error = it
                    })
                }catch(e: IllegalArgumentException){
                    error = "Заполните все поля"
                }catch (e:Exception){
                    crashlytics.log("auth_catch: $e")
                    error = "Ошибка"
                }
            }

            MainButton(text = "Зарегестророваться") {
                try {
                    error = ""

                    viewModel.registration(email,password,{
                        navController.navigate(Screen.Main.route)
                    },{
                        error = it
                    })
                }catch(e: IllegalArgumentException){
                    error = "Заполните все поля"
                }catch (e:Exception){
                    crashlytics.log("reg_catch: $e")
                    error = "Ошибка"
                }
            }
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
                (screenWidthDp / 1.6).toInt(),
                (screenHeightDp / 13),
                false
            ).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    5.dp
                )
                .height((screenHeightDp / 13).dp)
                .width((screenWidthDp / 1.6).dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(
                    5.dp
                )
                .height((screenHeightDp / 13).dp)
                .width((screenWidthDp / 1.6).dp)
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