package com.example.mvcplantapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.mvcplantapp.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mvcplantapp.model.PlantModel
import com.example.mvcplantapp.utils.Actions
import com.example.mvcplantapp.view_model.PlantViewModel
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAddPlantScreen(viewModel: PlantViewModel) {

    // Get the current context using LocalContext
    val context = LocalContext.current

    // Observa la URI de la imagen capturada desde el ViewModel
    val capturedImageUri by viewModel.capturedImageUri.observeAsState()

    val plantName = remember { mutableStateOf("") }
    val plantDescription = remember { mutableStateOf("") }
    val imageResId = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fondo de pantalla con una imagen
        Image(
            painter = painterResource(id = R.drawable.background_image5), // Cambia a tu recurso de imagen de fondo
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Escala la imagen para que cubra toda la pantalla
        )

        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // General screen padding
        ) {
            // Flecha en la esquina superior izquierda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(top = 25.dp)
                    .align(Alignment.TopStart), // Alinea el Row a la esquina superior izquierda
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24), // Reemplaza con tu recurso de flecha
                    contentDescription = "Back Icon", // Descripción accesible
                    modifier = Modifier
                        .clickable {
                            viewModel.action(Actions.ACTION_GO_TO_MAIN_SCREEN) // Acción para ir a la pantalla principal
                        }
                        .size(32.dp) // Tamaño del ícono
                        .padding(4.dp), // Espaciado alrededor del ícono para facilitar el clic
                    tint = Color.Black // Mantuve el color negro como estaba antes
                )
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Mostrar imagen capturada o el icono predeterminado
                if (capturedImageUri != null) {
                    // Mostrar la imagen capturada
                    Image(
                        painter = rememberAsyncImagePainter(model = capturedImageUri),
                        contentDescription = "Captured Plant Image",
                        modifier = Modifier
                            .size(200.dp) // Tamaño de la imagen
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(12.dp)), // Bordes redondeados
                        contentScale = ContentScale.Crop // Ajuste de la imagen
                    )
                } else {
                    // Mostrar el icono predeterminado
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_image_24), // Ícono de planta
                        contentDescription = "Plant Icon",
                        modifier = Modifier
                            .size(300.dp) // Tamaño del ícono
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // TextField para el nombre de la planta
                OutlinedTextField(
                    value = plantName.value,
                    onValueChange = { plantName.value = it },
                    label = { Text("Plant Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0x0051805E), // Color del borde al enfocar
                        unfocusedBorderColor = Color(0x0051805E), // Color del borde sin enfocar
                        containerColor = Color(0x57C0D9C5)
                    )
                )

                Spacer(modifier = Modifier.height(9.dp))

                // TextField para la descripción
                OutlinedTextField(
                    value = plantDescription.value,
                    onValueChange = { plantDescription.value = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0x0051805E), // Color del borde al enfocar
                        unfocusedBorderColor = Color(0x0051805E), // Color del borde sin enfocar
                        containerColor = Color(0x57C0D9C5)
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Botón para guardar
                Button(
                    onClick = {
                        if (plantName.value != "" && plantDescription.value != "") {
                            val newPlant = PlantModel(
                                id = System.currentTimeMillis().toString(), // Generar un ID único basado en el tiempo actual
                                name = plantName.value,
                                description = plantDescription.value,
                                //de momento dejamos el url vacio, se llena cuando se haya subido la planta a la BD
                                imageUrl = ""
                            )

                            // Pass the context along with the new plant
                            viewModel.action(Actions.ACTION_SAVE_NEW_PLANT, newPlant, context)
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
                    Text("Save")
                }
            }
        }
    }
}