package com.example.mvcplantapp.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.example.mvcplantapp.R
import com.example.mvcplantapp.view_model.PlantViewModel
import com.example.mvcplantapp.model.PlantModel
import com.example.mvcplantapp.utils.Actions
import com.example.mvcplantapp.ui.theme.MontserratFontFamily
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewMainScreen(viewModel: PlantViewModel) {
    val plants by viewModel.plants.observeAsState(emptyList()) // Correctly observe state

    val context = LocalContext.current

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri?.let { uri ->
                viewModel.action(Actions.ACTION_NEW_PLANT_BUTTON, uri)
            }
        }
    }


    val imageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(context.cacheDir, "plant_${System.currentTimeMillis()}.jpg")
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Your Plants",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {

                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Icon"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                viewModel.action(Actions.ACTION_SIGN_OUT)
                            },
                            text = { Text("Sign Out") }
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFFFFFFFF),
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    capturedImageUri = imageUri
                    cameraLauncher.launch(imageUri)
                },
                containerColor = Color(0xFF51805E),
                modifier = Modifier.size(70.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF)) // Set background color for the whole screen
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(plants) { plant -> // Use observed state here
                        PlantItem(plant, viewModel)
                    }
                }
            }
        }
    )
}



@Composable
fun PlantItem(plant: PlantModel, viewModel: PlantViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = (screenWidth / 2) - 20.dp


    val onClick = {
        viewModel.action(Actions.ACTION_PLANT_CLICKED, plant)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF))
            .padding(8.dp)
            .clickable(onClick = onClick) // Make the whole item clickable
    ) {


        val painter = rememberImagePainter(
            data = plant.imageUrl,
            builder = {
                crossfade(true) // Enable smooth image transition
                placeholder(R.drawable.placeholder) // A placeholder image while loading
                error(R.drawable.error_image) // Error image if loading fails
            }
        )

        Card(
            modifier = Modifier
                .width(imageWidth)
                .height(imageWidth) // Making the image square
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Image(
                painter = painter,
                contentDescription = plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            BasicText(
                text = plant.name,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
