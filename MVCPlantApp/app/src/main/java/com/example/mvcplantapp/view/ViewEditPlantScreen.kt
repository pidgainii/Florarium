package com.example.mvcplantapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mvcplantapp.R
import com.example.mvcplantapp.model.PlantModel
import com.example.mvcplantapp.utils.Actions
import com.example.mvcplantapp.view_model.PlantViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun ViewEditPlantScreen(viewModel: PlantViewModel) {
    val plant by viewModel.plant.observeAsState(PlantModel(id = "", name = "Unknown", description = "No Description", imageUrl = ""))
    var updatedName by remember { mutableStateOf(plant.name) }
    var updatedDescription by remember { mutableStateOf(plant.description) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = ((screenWidth / 5) * 4) - 10.dp
    val textPadding = ((screenWidth - imageWidth) / 2) - 12.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .clickable {
                        viewModel.action(Actions.ACTION_GO_TO_PLANT_SCREEN)
                    }
                    .size(32.dp)
                    .padding(4.dp),
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            // Plant image
            val painter = rememberAsyncImagePainter(plant.imageUrl)
            Image(
                painter = painter,
                contentDescription = plant.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 100.dp)
                    .size(imageWidth)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))


            OutlinedTextField(
                value = updatedName,
                onValueChange = { updatedName = it },
                label = { Text("Plant Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textPadding),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = updatedDescription,
                onValueChange = { updatedDescription = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textPadding),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(30.dp))


            Button(
                onClick = {
                    if (updatedName.isNotBlank() && updatedDescription.isNotBlank()) {
                        val updatedPlant = plant.copy(
                            name = updatedName,
                            description = updatedDescription
                        )
                        viewModel.action(Actions.ACTION_SAVE_PLANT_CHANGES, updatedPlant)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF51805E),
                    contentColor = Color.White
                )
            ) {
                Text("Save Changes")
            }
        }
    }
}
