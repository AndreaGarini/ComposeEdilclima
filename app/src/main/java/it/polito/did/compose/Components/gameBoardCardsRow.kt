package it.polito.did.compose.Components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.polito.did.compose.GameModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun gameBoardCardsRow(gm : GameModel, team : String){

    val playedCards = gm.playedCardsPerTeam.observeAsState()

    Row(modifier = Modifier.fillMaxSize()) {

        for (cardCounter in 0 until gm.gameLogic.months.size){

            Card(modifier = Modifier
                .fillMaxHeight()
                .weight(1f / gm.gameLogic.months.size)) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                    val month : String = gm.gameLogic.months[cardCounter]
                    if (playedCards.value!!.get(team)==null || playedCards.value!!.get(team)!!.get(month)==null)
                    {
                        Text(text = "no card")
                        // non c'è value per playedCards (transizioni fra livelli) o non c'è una carta giocata per il mese
                    }
                    else {
                        Text(text = playedCards.value!!.get(team)!!.get(month)!!)
                        // c'è value per playedCards e c'è una carta giocata per il mese
                    }
                }

            }
        }
    }
}