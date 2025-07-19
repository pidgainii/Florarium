package com.example.mvcplantapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.mvcplantapp.R
import com.example.mvcplantapp.utils.Actions
import com.example.mvcplantapp.view_model.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAuthenticationScreen(viewModel: PlantViewModel) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observa la variable de error
    val error = viewModel.errorAuthentication.observeAsState()
    // Observa la variable de error
    val success = viewModel.successAuthentication.observeAsState()

    // Observa para saber si tiene que reiniciarse (se reinicia si reload cambia de valor)
    val reload = viewModel.reloadAuthenticationScreen.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.background_image12),  // Reemplaza con tu imagen de fondo en drawable
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize() // Asegura que la imagen cubra todo el espacio
                .zIndex(-1f),  // Coloca la imagen detrás de los otros elementos
            contentScale = ContentScale.Crop  // Asegura que la imagen cubra completamente el área sin distorsionarse
        )



        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(130.dp))


            // Logo de la app (ajusta el tamaño según sea necesario)
            Image(
                painter = painterResource(id = R.drawable.ic_logo),  // Reemplaza con tu logo en drawable
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()  // Esto hace que el logo ocupe todo el ancho
                    .height(170.dp)  // Ajusta la altura deseada
                    .padding(horizontal = 32.dp)  // Agrega un poco de espacio de los lados
                    .padding(bottom = 32.dp)  // Espacio debajo del logo
                    .padding(top = 40.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // TextField para el correo electrónico
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF51805E),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    containerColor = Color(0x57C0D9C5)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TextField para la contraseña
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF51805E),
                    unfocusedBorderColor = Color(0xFFBDBDBD),
                    containerColor = Color(0x57C0D9C5)
                ),
                visualTransformation = PasswordVisualTransformation() // Oculta la contraseña
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Log in
            Box(
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                contentAlignment = Alignment.Center // Centra el contenido (el botón)
            ) {
                Button(
                    onClick = {
                        if (email.value != "" && password.value != "") {
                            val pair = Pair(email.value, password.value)
                            viewModel.action(Actions.ACTION_LOG_IN_ATTEMPT, pair)
                        }
                    },
                    modifier = Modifier
                        .width(150.dp) // Ajusta el ancho del botón
                        .padding(horizontal = 16.dp)
                        .height(35.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF51805E),
                        contentColor = Color.White
                    )
                ) {
                    Text("Log in", fontSize = 12.sp)
                }
            }


            Spacer(modifier = Modifier.height(9.dp))

            Box(
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                contentAlignment = Alignment.Center // Centra el contenido (el botón)
            ) {
                Button(
                    onClick = {
                        if (email.value != "" && password.value != "") {
                            val pair = Pair(email.value, password.value)
                            viewModel.action(Actions.ACTION_REGISTER_ATTEMPT, pair)
                        }
                    },
                    modifier = Modifier
                        .width(150.dp) // Ajusta el ancho del botón
                        .padding(horizontal = 16.dp)
                        .height(35.dp),
                    shape = RoundedCornerShape(20.dp), // Ajusta el radio de la esquina del botón
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF51805E),
                        contentColor = Color.White
                    )
                ) {
                    Text("Register", fontSize = 12.sp)
                }
            }

        }

        // Mostrar el mensaje de error si existe
        error.value?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = {
                    viewModel.action(Actions.ACTION_ERROR_ACCEPT) // Limpia el error cuando se cierra el dialogo
                },
                confirmButton = {
                    Button(onClick = { viewModel.action(Actions.ACTION_ERROR_ACCEPT) }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Authentication error") },
                text = { Text(errorMessage) }
            )
        }

        // Mostrar el mensaje de success si existe
        success.value?.let { successMessage ->
            AlertDialog(
                onDismissRequest = {
                    viewModel.action(Actions.ACTION_SUCCESS_ACCEPT) // Limpia el error cuando se cierra el dialogo
                },
                confirmButton = {
                    Button(onClick = { viewModel.action(Actions.ACTION_SUCCESS_ACCEPT) }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Success") },
                text = { Text(successMessage) }
            )
        }

        // Vaciar los campos de texto cuando reload cambia de valor
        reload.value?.let {
            LaunchedEffect(it) {
                email.value = ""
                password.value = ""
            }
        }
    }
}
