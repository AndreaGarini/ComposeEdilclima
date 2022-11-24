package it.polito.did.compose.Components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.did.compose.GameModel
import it.polito.did.compose.Screen.cardSelectionScreen
import it.polito.did.compose.Screen.retriveCardScreen
import it.polito.did.compose.Screen.teamsInfoScreen

@Composable
fun MainScreenContent(navController: NavHostController, portraitOrientation : Boolean, gm : GameModel, padding : PaddingValues) {

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = padding.calculateBottomPadding()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(0.05f)){
            infoRow(gm = gm)
        }

        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .weight(0.95f)) {
            NavHost(navController = navController, startDestination = "cardSelectionScreen") {
                composable("cardSelectionScreen") {
                    cardSelectionScreen(navController = navController, portrait = portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("retriveCardScreen") {
                    retriveCardScreen(navController = navController, portrait = portraitOrientation, gm, maxWidth, maxHeight)
                }
                composable("teamsInfoScreen") {
                    teamsInfoScreen(navController = navController, portrait = portraitOrientation, gm, maxWidth, maxHeight)
                }
            }
        }
    }
}