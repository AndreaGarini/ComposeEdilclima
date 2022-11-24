package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.polito.did.compose.GameModel

@Composable
fun infoRow(gm : GameModel){

    var timeCounter = gm.timerCountdown.observeAsState()
    val stats = gm.teamsStats.observeAsState()

    Row(modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
            Text(text = gm.team)
        }

        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
             Text(text = "B: ${stats.value?.get(gm.team)?.budget}")
        }

        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
                if (timeCounter==null){
                    //todo : animazione di attesa turno del timer (tipo loading)
                }
                else Text(text = "${timeCounter.value}")
        }
    }
}