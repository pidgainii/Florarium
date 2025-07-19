package com.example.mvcplantapp


import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mvcplantapp.view.AppNavigation
import com.example.mvcplantapp.view_model.PlantViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mvcplantapp.utils.Actions


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }



        setContent {
            val viewModel: PlantViewModel = viewModel()
            viewModel.action(Actions.ACTION_INITIALIZE_APP)
            AppNavigation(viewModel)
        }
    }
}
