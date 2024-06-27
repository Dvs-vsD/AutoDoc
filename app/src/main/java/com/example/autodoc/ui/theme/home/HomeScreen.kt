package com.example.autodoc.ui.theme.home

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.autodoc.MainActivity
import com.example.autodoc.R
import com.example.autodoc.Utils
import com.example.autodoc.ui.theme.home.viewModel.DocumentScannerViewModel

@Composable
fun HomeScreen(
    onScanButtonClicked: () -> Unit, viewModel: DocumentScannerViewModel, context: Context
) {
    val result by viewModel.result.observeAsState(null)

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) {
            Heading(modifier = Modifier.align(Alignment.TopCenter))
            if (result != null && result!!.pdf != null && viewModel.getImages().isNotEmpty()) {
                //TODO: Improve the file name logic
                SharePdfImages(
                    modifier = Modifier.align(Alignment.Center),
                    viewModel,
                    context,
                    viewModel.getImages(),
                    viewModel.getFileNameFromUri(result!!.pdf!!.uri.path!!)
                )
            } else {
                viewModel.clearCache(context)
                ButtonScan(modifier = Modifier.align(Alignment.Center), onScanButtonClicked)
            }
            BottomBar(modifier = Modifier.align(Alignment.BottomCenter), viewModel, context)
        }
    }
}

@Composable
fun Heading(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.raven),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Image(
                painter = painterResource(id = R.drawable.raven_bird),
                contentDescription = "raven logo",
                modifier = modifier
                    .size(128.dp)
                    .padding(bottom = 48.dp)
            )
        }

        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(id = R.string.title_description),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light,
            )
            Icon(
                painter = painterResource(id = R.drawable.feather),
                contentDescription = "feather icon",
                modifier = modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ButtonScan(modifier: Modifier, onScanButtonClicked: () -> Unit) {
    Button(
        onClick = onScanButtonClicked, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.scrim
        ), modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.btn_scan_now),
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(16.dp)
        )
    }
}

@Composable
fun BottomBar(modifier: Modifier, viewModel: DocumentScannerViewModel, context: Context) {
    Text(
        text = stringResource(id = R.string.minimalism_quote) + " - v" + viewModel.getAppVersion(
            context = context
        ),
        fontWeight = FontWeight.Light,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun SharePdfImages(
    modifier: Modifier,
    viewModel: DocumentScannerViewModel,
    context: Context,
    images: List<Uri>,
    pdfName: String?
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = modifier, colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ), elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.text_files_are_ready),
                        fontWeight = FontWeight.Light,
                        modifier = modifier
                            .padding(16.dp)
                            .weight(1f),
                    )
                    IconButton(onClick = { viewModel.clearResult() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }

                Spacer(modifier = modifier.size(16.dp))

                Box(modifier = modifier) {
                    Button(
                        onClick = { viewModel.sharePdf(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_share_pdf),
                            fontWeight = FontWeight.Bold,
                            modifier = modifier.padding(16.dp)
                        )
                    }
                    Utils.loadBitmapFromUri(context, images[0])?.asImageBitmap()?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "pdf",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp, 64.dp)
                                .offset(x = (-40).dp)
                                .graphicsLayer(rotationZ = -15f)
                                .zIndex(1f)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.pdf_icon),
                        contentDescription = "pdf",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(48.dp, 64.dp)
                            .padding(8.dp)
                            .offset(x = (-40).dp)
                            .graphicsLayer(rotationZ = -15f)
                            .zIndex(1f)
                    )
                }

                Spacer(modifier = modifier.size(32.dp))

                Box(modifier = modifier) {
                    Button(
                        onClick = { viewModel.shareImages(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                    ) {
                        Text(
                            text = stringResource(id = R.string.btn_share_jpg),
                            fontWeight = FontWeight.Bold,
                            modifier = modifier.padding(16.dp)
                        )
                    }
                    Utils.loadBitmapFromUri(context, images[0])?.asImageBitmap()?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "pdf",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp, 64.dp)
                                .offset(x = (-40).dp)
                                .graphicsLayer(rotationZ = -15f)
                                .zIndex(1f)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                    if (images.size >= 2) {
                        Utils.loadBitmapFromUri(context, images[1])?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "pdf",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp, 64.dp)
                                    .offset(x = (-20).dp)
                                    .zIndex(1f)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = modifier.size(16.dp))
        if (pdfName != null) {
            Text(text = pdfName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Light)
            Spacer(modifier = modifier.size(2.dp))
        }

        Button(
            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.scrim
            )
        ) {
            Text(text = "Rename")

            Spacer(modifier = modifier.size(4.dp))

            Icon(imageVector = Icons.Default.Edit, contentDescription = "Rename")
        }
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun PreviewHome() {
    HomeScreen(onScanButtonClicked = {}, DocumentScannerViewModel(), MainActivity())
}

@Composable
fun RenamePdf(modifier: Modifier) {
    var text by remember { mutableStateOf("") }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = text,
            onValueChange = { name ->
                text = name
            },
            label = { Text(stringResource(id = R.string.rename_pdf)) },
        )

        Spacer(modifier = modifier.size(8.dp))

        Button(
            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.scrim
            )
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "Rename Done")
        }
    }
}

@Preview(widthDp = 320, device = Devices.PIXEL_3A)
@Composable
fun PreviewRename() {
    RenamePdf(modifier = Modifier)
}