package com.Quotes.Great.ui.screens.authScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Quotes.Great.LocalNavController
import com.Quotes.Great.R
import com.Quotes.Great.navigation.Screen
import com.Quotes.Great.ui.screens.mainScreen.MainButton
import com.Quotes.Great.ui.theme.primaryText
import com.Quotes.Great.ui.theme.secondaryBackground
import com.Quotes.Great.ui.theme.tintColor
import com.Quotes.Great.ui.view.YandexAdsBanner
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            YandexAdsBanner(
                size = AdSize.BANNER_240x400
            )
        }

        item {

            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(5.dp)
            )

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
                viewModel.signIn(email.trim(),password.trim(),{
                    navController.navigate(Screen.Main.route)
                },{
                    error = it
                })
            }

            MainButton(text = "Зарегестророваться") {
                viewModel.registration(email,password,{
                    navController.navigate(Screen.Main.route)
                },{
                    error = it
                })
            }
        }
    }
}