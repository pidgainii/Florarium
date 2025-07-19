package com.example.mvcplantapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvcplantapp.R
import com.example.mvcplantapp.model.PlantModel
import com.example.mvcplantapp.utils.Actions
import com.example.mvcplantapp.view_model.PlantViewModel
import com.example.mvcplantapp.ui.theme.MontserratFontFamily
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ViewCheckPlantScreen(viewModel: PlantViewModel) {

    val plant by viewModel.plant.observeAsState(PlantModel(id = "", name = "Unknown", description = "No Description", imageUrl = ""))
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = ((screenWidth / 5) * 4) - 10.dp
    val textPadding = ((screenWidth - imageWidth) / 2) - 12.dp

    // States for the dropdown menu and confirmation dialog
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Box for layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Row for icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Icon
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .clickable {
                        viewModel.action(Actions.ACTION_GO_TO_MAIN_SCREEN)
                    }
                    .size(32.dp)
                    .padding(4.dp),
                tint = Color.Black
            )

            // Dropdown Menu Icon
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu Icon",
                    modifier = Modifier
                        .clickable { showMenu = true }
                        .size(32.dp)
                        .padding(4.dp),
                    tint = Color.Black
                )

                // Dropdown Menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            viewModel.action(Actions.ACTION_MODIFY_PLANT)
                        },
                        text = { Text("Modify") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            showDeleteConfirmationDialog = true
                        },
                        text = { Text("Delete") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            exportPdf(context, plant)
                        },
                        text = { Text("Export") }
                    )
                }
            }
        }

        // Column for plant elements
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            // Plant Image
            val painter = rememberAsyncImagePainter(plant.imageUrl)
            Image(
                painter = painter,
                contentDescription = plant.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 100.dp)
                    .size(imageWidth)
                    .clip(RoundedCornerShape(16.dp))
            )

            // Plant Name
            Text(
                text = plant.name,
                modifier = Modifier
                    .padding(top = 40.dp)
                    .padding(bottom = 8.dp)
                    .padding(horizontal = textPadding),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )

            // Plant Description
            Text(
                text = plant.description,
                modifier = Modifier
                    .padding(horizontal = textPadding),
                color = Color(0xFF333333),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }

    // Confirmation Dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = {
                Text(text = "Are you sure?")
            },
            text = {
                Text("Do you really want to delete this plant?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        viewModel.action(Actions.ACTION_DELETE_PLANT)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}



fun exportPdf(context: android.content.Context, plant: PlantModel) {
    val coroutineScope = (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope
    coroutineScope?.launch {
        // Fetch the bitmap in the IO dispatcher
        val bitmap = withContext(Dispatchers.IO) {
            fetchBitmapFromUrl(context, plant.imageUrl)
        }

        // Generate the PDF
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size: 595x842 pixels
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.textSize = 18f
        paint.color = Color.Black.toArgb()

        // PDF dimensions
        val pdfWidth = pageInfo.pageWidth
        val pdfHeight = pageInfo.pageHeight

        // Add the image at the upper center
        bitmap?.let {
            val imageWidth = 300 // Set desired image width
            val imageHeight = 300 // Set desired image height
            val scaledBitmap = Bitmap.createScaledBitmap(it, imageWidth, imageHeight, true)

            val imageX = (pdfWidth - imageWidth) / 2f // Center horizontally
            val imageY = 50f // Position near the top (50px from the top)
            canvas.drawBitmap(scaledBitmap, imageX, imageY, null)

            // Adjust the text position after the image
            paint.textSize = 22f
            canvas.drawText(" ${plant.name}", 50f, imageY + imageHeight + 50f, paint)

            // Draw description as multi-line text
            val descriptionStartY = imageY + imageHeight + 100f
            drawMultilineText(
                canvas,
                plant.description,
                paint,
                50f, // Start X
                descriptionStartY, // Start Y
                pdfWidth - 100f // Max text width
            )
        } ?: run {
            // Fallback if the image is null
            paint.textSize = 22f
            canvas.drawText(" ${plant.name}", 50f, 100f, paint)

            // Draw description as multi-line text
            val descriptionStartY = 150f
            drawMultilineText(
                canvas,
                plant.description,
                paint,
                50f, // Start X
                descriptionStartY, // Start Y
                pdfWidth - 100f // Max text width
            )
        }

        pdfDocument.finishPage(page)

        // Save the PDF to the Downloads directory
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${plant.name}.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            withContext(Dispatchers.IO) {
                resolver.openOutputStream(it).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
        }

        pdfDocument.close()
    }
}





suspend fun fetchBitmapFromUrl(context: android.content.Context, imageUrl: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .allowHardware(false) // To ensure compatibility with PDF drawing
        .build()

    val result = loader.execute(request)
    return if (result is SuccessResult) {
        (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
    } else {
        null
    }
}



private fun drawMultilineText(
    canvas: Canvas,
    text: String,
    paint: Paint,
    startX: Float,
    startY: Float,
    maxWidth: Float
) {
    val words = text.split(" ")
    var currentLine = ""
    var currentY = startY

    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        val textWidth = paint.measureText(testLine)

        if (textWidth <= maxWidth) {
            // If the test line fits, append the word
            currentLine = testLine
        } else {
            // If it doesn't fit, draw the current line and start a new one
            canvas.drawText(currentLine, startX, currentY, paint)
            currentLine = word
            currentY += paint.textSize + 10f // Move to the next line (adjust 10f for spacing)
        }
    }

    // Draw the last line
    if (currentLine.isNotEmpty()) {
        canvas.drawText(currentLine, startX, currentY, paint)
    }
}

