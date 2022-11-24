package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import it.polito.did.compose.GameModel
import it.polito.did.compose.R
import it.polito.did.compose.Screen.Direction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pagerCard(index: Int, gm: GameModel, cardPlayable : String, animateToStart: () -> Unit){

    val playedCards = gm.playedCardsPerTeam.observeAsState()

    var push by remember {
        mutableStateOf(pushResult.CardDown)
    }

    val onClick : () -> Unit = {
        //todo : fai un check sul timer per sapere se il giocatore può giocare
        //todo: se il timer va a 0 metti pushing a false
        push =
            when {
                cardPlayable.equals("null") -> pushResult.CardDown
                cardPlayable.equals("void") -> pushResult.InvalidCard
                else -> pushResult.Success .also{
                    val budget: Int = gm.getBudgetSnapshot(gm.playedCardsPerTeam.value!!.get(gm.team)!!.values.toList())
                    if (gm.gameLogic.cardsMap.get(cardPlayable)!!.money < budget){
                        gm.playCardInPos(index, cardPlayable)
                        animateToStart()
                        pushResult.CardDown
                    }
                    else push = pushResult.LowBudget
                }
            }
    }


    Row (modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

            if (playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[index]) == null){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)){
                    Button(onClick = onClick,
                        enabled = playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[index]) == null) {
                        Text(text = "play card")
                    }
                }
                Row (modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center){
                    when {
                        push == pushResult.InvalidCard -> Text(text = "carta non valida")
                        push == pushResult.CardDown || cardPlayable.equals("null") -> Text(text = "swipe up per selezionare")
                        push == pushResult.LowBudget -> Text(text = "budget esaurito")
                    }

                }
            }
            else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)){
                    Button(onClick = onClick,
                        enabled = playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[index]) == null) {
                        Text(text = "play card")
                    }
                }
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(2f)) {
            Card(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        val text = playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[index])
                        if(text != null)
                            Text(text = text)
                        else
                            Text(text = "no card")

                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Image(painter = painterResource(id = R.drawable.ic_android_black_24dp), contentDescription = "pagerCard")
                    }
                }
            }
        }
    }
}

enum class pushResult{
    Success, CardDown, InvalidCard, LowBudget
}
