package it.polito.did.compose.Screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.did.compose.GameModel

@Composable
fun cameraScreen (navController: NavController, portrait: Boolean, gm: GameModel, usableWidth : Dp, usableHeight : Dp) {

    //todo: fai in modo che se il player joina a partita iniziata non va,
    // ma se esce dalla partta dopo aver joinato può rientrare

    LaunchedEffect(key1 = Unit, block = {
        gm.listenToLevelChange()
    })
    val level = gm.level.observeAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { gm.joinMatch()}) {
            Text(text = "joinMatch")
        }
    }

    //todo: fai in modo che start match del master scriva 1prepared anzichè uno e i giocatori possano passare alla schermata di gioco senza che parta il timer
    //il timer parte quando il master preme start level 1

    if (level.value == 1L) {
        LaunchedEffect(key1 = Unit, block = {
            //todo: qui aggiungi un delay con la schermata di splash, in modo che i listener abbiano il tempo di settarsi
            //todo: fai un test senza connessione, per vedere se almeno non si rompe anche senza dati
            gm.playerReadyToPlay()
            navController.navigate("MainScreen")
        })
    }

}