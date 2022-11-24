package it.polito.did.compose.Screen

import android.graphics.drawable.GradientDrawable.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.did.compose.Components.MainScreenContent
import it.polito.did.compose.Components.bottomBar
import it.polito.did.compose.GameModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(portraitOrientation: Boolean, gm : GameModel){
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize(),
        content = { padding ->
                MainScreenContent(navController, portraitOrientation = portraitOrientation, gm = gm, padding)
        },
        bottomBar = { bottomBar(navController) }
    )

}