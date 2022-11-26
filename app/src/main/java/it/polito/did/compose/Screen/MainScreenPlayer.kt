package it.polito.did.compose.Screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import it.polito.did.compose.Components.MainScreenContent
import it.polito.did.compose.Components.bottomBar
import it.polito.did.compose.GameModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenPlayer(portraitOrientation: Boolean, gm : GameModel){
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize(),
        content = { padding ->
                MainScreenContent(navController, portraitOrientation = portraitOrientation, gm = gm, padding)
        },
        bottomBar = { bottomBar(navController) }
    )

}