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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.polito.did.compose.DataClasses.researchSet
import it.polito.did.compose.GameModel
import it.polito.did.compose.R
import it.polito.did.compose.Screen.Direction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pagerCard(index: Int, gm: GameModel, cardPlayable : String, animateToStart: () -> Unit, pagerCardModifier : Modifier){

    val playedCards = gm.playedCardsPerTeam.observeAsState()

    val ableToPlay = gm.playerTimer.observeAsState()

    val researchNeededCheck : (String) -> Boolean = {

        val resNeeded = gm.gameLogic.cardsMap.get(it)?.research == researchSet.Needed
        val allResPlayed = if(resNeeded) gm.playedCardsPerTeam.value!!.get(gm.team)!!.values.containsAll( gm.gameLogic.cardsMap.get(it)?.resCard!!)
        else false
        !((resNeeded && allResPlayed) || !resNeeded)
    }

    val resNeededCodeString : (String) -> String = {
        var exitString = ""
        gm.gameLogic.cardsMap.get(it)!!.resCard!!.forEach {
            if (!gm.playedCardsPerTeam.value!!.get(gm.team)!!.values.contains(it)){
                exitString = exitString.plus("${it} ")
            }
        }
        exitString
    }

    val onClick : () -> Unit = {
        gm.pushResult.value =
            when {
                cardPlayable.equals("null") -> pushResult.CardDown to null
                cardPlayable.equals("void") -> pushResult.InvalidCard to null
                researchNeededCheck(cardPlayable) -> pushResult.ResearchNeeded to resNeededCodeString (cardPlayable)
                else -> pushResult.Success to null .also{
                    val budget: Int = gm.getBudgetSnapshot(gm.playedCardsPerTeam.value!!.get(gm.team)!!.values.toList())
                    if (gm.gameLogic.cardsMap.get(cardPlayable)!!.money < budget){
                        gm.playCardInPos(index, cardPlayable)
                        animateToStart()
                        pushResult.CardDown
                        gm.playerTimerCountdown.value = null
                        gm.playerTimer.value?.cancel()
                        gm.playerTimer.value = null
                        gm.setTimeOutTrue()
                    }
                    else gm.pushResult.value = pushResult.LowBudget to null
                }

            }
    }


    Row (modifier = pagerCardModifier){
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            Button(onClick = onClick,
                enabled = playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[index]) == null && ableToPlay.value!=null) {
                Text(text = "play card")
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
    Success, CardDown, InvalidCard, LowBudget, ResearchNeeded
}
