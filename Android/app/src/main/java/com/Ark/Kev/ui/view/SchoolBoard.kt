package com.Ark.Kev.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ark.Kev.ui.theme.primaryText
import com.Ark.Kev.R

@Composable
fun SchoolBoard(
    text:String,
    width:Double,
    height:Double
) {
    val context = LocalContext.current

    Box {
        Image(
            bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.resources, R.drawable.school_board),
                width.toInt(),
                height.toInt(),
                false
            ).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(width=width.dp,height=height.dp)
                .padding(start = 5.dp)
        )

        Column(
            Modifier
                .size(width=width.dp,height=height.dp)
                .padding(start = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            val height = if(text.count() < 20) 20.dp else 40.dp
            val textSize = if(text.count() < 20) 22.sp else 18.sp

            Text(
                text = text,
                color = primaryText,
                modifier = Modifier
                    .widthIn(max = (width-15).dp)
                    .heightIn(max = (height-15).dp),
                textAlign = TextAlign.Center,
                fontSize = textSize,
                fontStyle = FontStyle.Italic
            )
        }
    }
}