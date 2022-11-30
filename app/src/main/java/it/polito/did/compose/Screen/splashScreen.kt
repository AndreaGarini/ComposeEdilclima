package it.polito.did.compose.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.did.compose.DataClasses.dialogData
import it.polito.did.compose.GameModel
import kotlinx.coroutines.delay

@Composable
fun splashScreen(gm : GameModel, navController: NavController){

    val levelCount = gm.playerLevelCounter.observeAsState()
    val levelStatus = gm.playerLevelStatus.observeAsState()

    LaunchedEffect(key1 = Unit, block = {
        //todo: fai un test senza connessione, per vedere se almeno non si rompe anche senza dati
        gm.playerReadyToPlay()
        delay(1000)
        gm.splash.value = false
        val data = dialogData("level${gm.playerLevelCounter.value}", null, true, null, "")
        gm.showDialog.value = data
        gm.showDialog.value
    })

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Cyan), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "splash", fontSize = 40.sp, color = Color.White)
    }

    if (levelCount.value!! == 1L && levelStatus.value!! != null) {
        LaunchedEffect(key1 = Unit, block = {
            navController.navigate("MainScreen")
        })
    }
}