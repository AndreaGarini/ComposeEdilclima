package it.polito.did.compose.Screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center) {
        Button(onClick = { gm.joinMatch()}) {
            Text(text = "joinMatch")
        }
    }

}