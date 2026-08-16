package com.proxy.gshttp


import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheetDialog(
    version: String,
    sheetState: SheetState,
    strings: Map<String, String>,
    bgColor: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    lineColor: Color,
    uriHandler: androidx.compose.ui.platform.UriHandler,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = bgColor,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(textColorPrimary.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = strings["info"] ?: "Information",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(textColorPrimary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gs_ht),
                    contentDescription = "Аватар",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                "GS HTTP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings["developer"] ?: "Developer: Georgy Smerdov",
                fontSize = 14.sp,
                color = textColorSecondary
            )
            Text(
                text = "${strings["version"] ?: "Version"}: $VERSION",
                fontSize = 12.sp,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings["downloaded_from"] ?: "Downloaded from: GitHub",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColorSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = lineColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = {
                uriHandler.openUri("https://gs-ht.ru/PRIVACY_GS.HTTP_EN.html")

            }) {
                Text(
                    text = strings["privacy_policy"] ?: "https://gs-ht.ru/",
                    color = textColorPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = lineColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = {
                uriHandler.openUri("https://github.com/proto-gs/GS.HTTP")

            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        tint = textColorPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings["source_code"] ?: "Source Code",
                        color = textColorPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
