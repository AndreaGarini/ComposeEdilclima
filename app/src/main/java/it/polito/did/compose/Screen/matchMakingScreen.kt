package it.polito.did.compose.Screen

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.did.compose.GameModel

@Composable
fun matchMakingScreen (navController: NavController, portrait: Boolean, gm: GameModel, usableWidth : Dp, usableHeight : Dp) {

    LaunchedEffect(key1 = Unit, block = {
        gm.setPlayerCounter()
    })

    val startMatch = gm.startMatch.observeAsState()
    val playerCount = gm.playerCounter.observeAsState()

    
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    gm.createNewMatch()
                }
            ){
                Text(text = "create new match")
            }

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "connected players : ")
            Text(text = "${playerCount.value}")

        }

        if(startMatch.value!!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        gm.startMatch(1)
                    }
                ) {
                    Text(text = "start match")
                }
            }

        else
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        gm.prepareMatch()
                    }
                ){
                    Text(text = "prepare match")
                }
            }
    }

}