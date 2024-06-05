package com.startapp.demo.compose

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.startapp.sdk.ads.nativead.NativeAdDetails

@Composable
fun NativeAdLayout(ad: NativeAdDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // NOTE extremely important to specify .height(IntrinsicSize.Max) in order to make the native ad clickable
            .height(IntrinsicSize.Max)
            .background(color = Color(0xffefefef))
    ) {
        Row {
            if (ad.imageBitmap != null) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(160.dp)
                        .padding(16.dp),
                ) {
                    Image(
                        bitmap = ad.imageBitmap.asImageBitmap(),
                        contentDescription = "Ad image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (ad.imageBitmap != null) 0.dp else 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                    ),
            ) {
                Text(
                    text = ad.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Rating: %.1f⭐".format(ad.rating),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Category: %s".format(ad.category),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = ad.description,
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (ad.callToAction.isNotBlank()) {
                    // NOTE onClick is configured inside AndroidView after the registerViewForInteraction()
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { /* do nothing */ },
                    ) {
                        Text(ad.callToAction)
                    }
                }
            }
        }

        Text(
            text = "Ad",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .border(1.dp, Color(0xff888888))
                .padding(horizontal = 8.dp, vertical = 1.dp),
        )

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val result = View(context)
                ad.registerViewForInteraction(result)
                return@AndroidView result
            },
        )
    }
}
