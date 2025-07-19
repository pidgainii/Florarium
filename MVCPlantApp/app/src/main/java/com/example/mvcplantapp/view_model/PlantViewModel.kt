package com.example.mvcplantapp.view_model


import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvcplantapp.model.PlantModel
import com.example.mvcplantapp.model.PlantRepositoryImpl
import com.example.mvcplantapp.model.UserRepositoryImpl
import com.example.mvcplantapp.utils.Actions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



//enumerado que nos va a indicar
//en que pantalla tiene que estar la aplicacion
enum class Screen {
    MainScreen,
    AddPlantScreen,
    ViewPlantScreen,
    AuthenticationScreen,
    EditPlantScreen
}





class PlantViewModel : ViewModel() {
    //tenemos _plants que es privado y solo lo puede modificar PlantViewModel
    //y tenemos plants que es una variable que la observan las vistas para actualizarse
    private val _plants = MutableLiveData<List<PlantModel>>(emptyList())
    val plants: LiveData<List<PlantModel>> get() = _plants

    //Lo mismo con la pantalla actual (en este caso sera MyApp quien
    //observe esta variable para saber si cambiar de pantalla
    private val _currentScreen = MutableLiveData<Screen>(Screen.MainScreen)
    val currentScreen: LiveData<Screen> get() = _currentScreen


    //Esta es para saber que planta se ha pulsado, para la ViewCheckPlantScreen
    private val _plant = MutableLiveData<PlantModel>()
    val plant: LiveData<PlantModel> get() = _plant


    //Esta variable sera observada por las vistas
    private val _errorAuthentication = MutableLiveData<String?>()
    val errorAuthentication: LiveData<String?> get() = _errorAuthentication


    //Esta variable sera observada por las vistas
    private val _successAuthentication = MutableLiveData<String?>()
    val successAuthentication: LiveData<String?> get() = _successAuthentication

    //Esta variable es para que la vista de autenticacion sepa si tiene que reiniciarse
    private val _reloadAuthenticationScreen = MutableLiveData<Boolean?>()
    val reloadAuthenticationScreen: LiveData<Boolean?> get() = _reloadAuthenticationScreen

    //Variable donde se almacenara el uri de la foto mientras el usuario rellena los datos.
    private val _capturedImageUri = MutableLiveData<Uri?>()
    val capturedImageUri: LiveData<Uri?> = _capturedImageUri




    //esta es la funcion que usaran las vistas para comunicar que
    //el usuario a realizado una accion
    fun action(actionID: Int, data: Any? = null, context: Context? = null)
    {
        when(actionID) {
            Actions.ACTION_INITIALIZE_APP ->
            {
                _currentScreen.value = Screen.AuthenticationScreen
                _reloadAuthenticationScreen.value = true
            }

            Actions.ACTION_LOG_IN_ATTEMPT ->
            {
                val pair = data as? Pair<*, *>
                if (pair != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val hasError = UserRepositoryImpl.login(
                            pair.first.toString(),
                            pair.second.toString()
                        )
                        if (hasError) {
                            _errorAuthentication.value = "Login failed"
                        } else {
                            loadPlants()
                            _currentScreen.value = Screen.MainScreen
                        }
                    }
                }
            }

            Actions.ACTION_REGISTER_ATTEMPT ->
            {
                val pair = data as? Pair<*, *> // Safe cast to Pair
                if (pair != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val hasError = UserRepositoryImpl.register(
                            pair.first.toString(),
                            pair.second.toString()
                        )
                        if (hasError) {
                            _errorAuthentication.value = "Registered failed"
                        } else {
                            _successAuthentication.value = "Registration succeed"
                            //lo modificamos, para que se vacien los campos de texto
                            _reloadAuthenticationScreen.value = !(_reloadAuthenticationScreen.value ?: false)
                        }
                    }
                }
            }


            //estamos en la pantalla principal y queremos aniadir nueva planta
            Actions.ACTION_NEW_PLANT_BUTTON ->
            {
                //se guarda el uri de la foto mientras el usuario rellena los datos de la planta
                val uri: Uri? = data as? Uri
                _capturedImageUri.value = uri
                _currentScreen.value = Screen.AddPlantScreen
            }


            //hemos rellenado los textfield y dado a "save"
            Actions.ACTION_SAVE_NEW_PLANT ->
            {
                val newPlant: PlantModel = data as PlantModel
                val nonNullContext = checkNotNull(context)
                addPlant(newPlant, nonNullContext)


                _capturedImageUri.value = null
                _currentScreen.value = Screen.MainScreen
            }

            //se ha pulsado el boton de volver desde la pantalla
            //de aniadir planta
            Actions.ACTION_GO_TO_MAIN_SCREEN ->
            {
                _currentScreen.value = Screen.MainScreen
            }

            Actions.ACTION_PLANT_CLICKED ->
            {
                val plantvar: PlantModel = data as PlantModel
                _plant.value = plantvar
                _currentScreen.value = Screen.ViewPlantScreen
            }

            // Cuando una vista ha mostrado un mensaje de error, y el usuario le ha dado a "ok"
            // borramos el mensaje de error
            Actions.ACTION_ERROR_ACCEPT ->
            {
                _errorAuthentication.value = null
            }

            Actions.ACTION_SUCCESS_ACCEPT ->
            {
                _successAuthentication.value = null
            }

            Actions.ACTION_MODIFY_PLANT ->
            {
                _currentScreen.value = Screen.EditPlantScreen
            }

            Actions.ACTION_GO_TO_PLANT_SCREEN ->
            {
                _currentScreen.value = Screen.ViewPlantScreen
            }

            Actions.ACTION_SAVE_PLANT_CHANGES ->
            {
                val newPlant: PlantModel = data as PlantModel
                updatePlant(newPlant)
                _currentScreen.value = Screen.ViewPlantScreen
            }

            Actions.ACTION_DELETE_PLANT ->
            {
                val plantId = _plant.value?.id ?: "" // Safe access, provides default empty string if needed
                deletePlant(plantId)
                loadPlants()
                _plant.value = PlantModel()
                _currentScreen.value = Screen.MainScreen
            }

            Actions.ACTION_SIGN_OUT ->
            {
                if (signOut()) _currentScreen.value = Screen.AuthenticationScreen
            }

            else ->
            {

            }
        }
    }










    private fun loadPlants() {
        viewModelScope.launch {
            _plants.value = PlantRepositoryImpl.getAllPlants()
        }
    }

    private fun addPlant(plant: PlantModel, context: Context) {
        viewModelScope.launch {
            // Retrieve the photo URI
            val photouri: Uri = _capturedImageUri.value ?: Uri.parse("")

            // Call the repository to add the plant
            val success = PlantRepositoryImpl.addPlant(plant, photouri, context)

            // Optionally handle the result here (e.g., show a message or handle success/failure)
            if (success) {
                _plants.value = PlantRepositoryImpl.getAllPlants()
            }
        }
    }

    // Delete a plant
    private fun deletePlant(plantId: String) {
        viewModelScope.launch {
            PlantRepositoryImpl.deletePlant(plantId)
        }
    }

    private fun updatePlant(plant: PlantModel)
    {
        viewModelScope.launch {
            val success = PlantRepositoryImpl.updatePlant(plant)
            if (success)
            {
                _plant.value = plant
                _plants.value = PlantRepositoryImpl.getAllPlants()
            }
        }
    }

    private fun signOut(): Boolean
    {
        return UserRepositoryImpl.signOut()
    }
}