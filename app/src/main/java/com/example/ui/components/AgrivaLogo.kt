package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AgrivaYellow

@Composable
fun AgrivaLogo(
    modifier: Modifier = Modifier,
    size: Float = 100f, // Outer size
    showText: Boolean = true,
    textColor: Color = Color.Unspecified,
    textSize: Float = 24f
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // High quality combined Madagascar outline + Leaf icon vector
        Image(
            painter = painterResource(id = R.drawable.ic_agriva_logo),
            contentDescription = "Agriva Mada Brand Logo",
            modifier = Modifier.size((size * 0.85).dp)
        )

        if (showText) {
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "AGRIVA",
                    color = if (textColor == Color.Unspecified) MaterialTheme.colorScheme.primary else textColor,
                    fontSize = textSize.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "MADA",
                    color = AgrivaYellow,
                    fontSize = (textSize * 0.7f).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

