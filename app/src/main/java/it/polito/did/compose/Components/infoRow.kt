package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.polito.did.compose.GameModel

@Composable
fun infoRow(gm : GameModel){

    val stats = gm.teamsStats.observeAsState()

    Log.d("stats in info row :", stats.value!!.toString())

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
             Text(text = "B: ${stats.value!!.get(gm.team)!!.budget}") //todo: togli i non null asserted e fai i null check perchè crasha alle volte
        }

        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)) {
                //todo : timer qui
        }
    }
}