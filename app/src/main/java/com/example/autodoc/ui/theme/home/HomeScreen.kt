package com.example.autodoc.ui.theme.home

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.autodoc.MainActivity
import com.example.autodoc.R
import com.example.autodoc.Utils
import com.example.autodoc.ui.theme.AutoDocTheme
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
            if (result != null && viewModel.result.value?.pdf != null && viewModel.getImages().isNotEmpty()) {
                //TODO: Improve the file name logic
                SharePdfImages(
                    modifier = Modifier.align(Alignment.Center),
                    viewModel,
                    context,
                    viewModel.getImages(),
                    viewModel.pdf.value?.name
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

                ButtonSharePdf(
                    modifier = modifier,
                    viewModel = viewModel,
                    context = context,
                    images = images
                )

                Spacer(modifier = modifier.size(32.dp))

                ButtonShareImages(
                    modifier = modifier,
                    viewModel = viewModel,
                    context = context,
                    images = images
                )
            }
        }

        Spacer(modifier = modifier.size(16.dp))
        ButtonRename(pdfName, modifier, viewModel, context)
    }
}

@Composable
fun ButtonSharePdf(
    modifier: Modifier,
    viewModel: DocumentScannerViewModel,
    context: Context,
    images: List<Uri>
) {
    Row {
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

        //TODO: ASK PERMISSION FOR ANDROID 9 AND BELOW
        IconButton (
            onClick = { viewModel.savePdfToDevice(context) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.scrim
            ),
            modifier = modifier.size(64.dp).padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_download_24),
                contentDescription = "",
                tint = Color.White,
                modifier = modifier.padding(16.dp)
            )
        }
    }

}

@Composable
fun ButtonShareImages(
    modifier: Modifier,
    viewModel: DocumentScannerViewModel,
    context: Context,
    images: List<Uri>
) {
    Row {
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

        //TODO: ASK PERMISSION FOR ANDROID 9 AND BELOW
        IconButton (
            onClick = { viewModel.saveImagesToGallery(context) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.scrim
            ),
            modifier = modifier.size(64.dp).padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_download_24),
                contentDescription = "",
                tint = Color.White,
                modifier = modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ButtonRename(pdfName: String?, modifier: Modifier, viewModel: DocumentScannerViewModel, context: Context) {
    var showDialog by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var name by remember {
        mutableStateOf(pdfName ?: "Raven_${System.currentTimeMillis()}")
    }
    var text by remember { mutableStateOf(name.removeSuffix(".pdf")) }
    val errorMessage =
        "Invalid file name. Please ensure it doesn't contain special characters and isn't empty."

    Row {
        Text(
            text = "file name: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = modifier.size(2.dp))

    Button(
        onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.scrim
        )
    ) {
        Text(text = "Rename")

        Spacer(modifier = modifier.size(4.dp))

        Icon(imageVector = Icons.Default.Edit, contentDescription = "Rename")
    }

    if (showDialog) {
        RenamePDFDialog(
            text = text,
            onTextChange = { newText -> text = newText },
            onDismiss = { showDialog = false },
            onConfirm = {
                if (validatePdfName(text)) {
                    showDialog = false
                    isError = false
                    name = text
                    viewModel.copyAndRenameFile(context = context, text.plus(".pdf"))
                } else
                    isError = true
            },
            modifier = modifier,
            isError,
            errorMessage
        )
    }
}

@Composable
fun RenamePDFDialog(
    text: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier,
    isError: Boolean,
    errorMessage: String
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rename your PDF Document",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = modifier.height(8.dp))
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("Name", color = MaterialTheme.colorScheme.inverseSurface) },
                    suffix = {
                        Text(text = ".pdf")
                    },
                    singleLine = true,
                    isError = isError,
                    modifier = modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.scrim,
                        focusedIndicatorColor = MaterialTheme.colorScheme.scrim,
                    )
                )
                if (isError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier.weight(0.5F, true),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.scrim,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        modifier.weight(0.5F, true),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun SaveToDevice(modifier: Modifier) {

}

fun validatePdfName(name: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9 _-]+$")
    return name.isNotEmpty() && regex.matches(name)
}

@Preview(widthDp = 320, device = Devices.PIXEL_3A)
@Composable
fun PreviewRename() {
    AutoDocTheme {
        RenamePDFDialog(
            text = "passport",
            onTextChange = {},
            onDismiss = { },
            onConfirm = {},
            modifier = Modifier,
            true,
            "Invalid file name. Please ensure it doesn't contain special characters and isn't empty"
        )
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun PreviewHome() {
    AutoDocTheme {
        HomeScreen(
            onScanButtonClicked = {},
            fakeViewModel(), // Use a mock/fake ViewModel
            context = LocalContext.current // Provide a valid context for preview
        )
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
fun PreviewSharePdfImages() {
    val images = ArrayList<Uri>()
    images.add(Uri.parse("content://com.example.autodoc/fake_document.pdf")) //Fake Uri
    AutoDocTheme {
        SharePdfImages(
            modifier = Modifier,
            fakeViewModel(), // Use a mock/fake ViewModel
            context = LocalContext.current, // Provide a valid context for preview,
            images,
            ""
        )
    }
}

// Create a fake ViewModel for preview
@Composable
fun fakeViewModel(): DocumentScannerViewModel {
    return remember {
        DocumentScannerViewModel() // Replace with a constructor that doesn't require dependencies
    }
}

