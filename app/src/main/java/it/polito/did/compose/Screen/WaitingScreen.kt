package it.polito.did.compose.Screen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.did.compose.Components.bottomBar
import it.polito.did.compose.GameModel
import it.polito.did.compose.ui.theme.GameTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingScreen(portraitOrientation: Boolean, gm: GameModel){

    val navController = rememberNavController()
    val splash = gm.splash.observeAsState()

    if(splash.value!!){
        splashScreen(gm, navController)
    }
    else{
        BoxWithConstraints() {
            NavHost(navController = navController, startDestination = "initialScreen") {
                composable("initialScreen") {
                    initialScreen(navController = navController, portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("MainScreen") {
                    MainScreenPlayer(portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("matchMakingScreen") {
                    matchMakingScreen(navController = navController, portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("gameBoardScreen") {
                    gameBoardScreen(navController = navController, portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("cameraScreen") {
                    cameraScreen(navController = navController, portraitOrientation, gm, maxWidth, maxHeight)
                }
            }
        }
    }
}