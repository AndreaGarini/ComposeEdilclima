package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import it.polito.did.compose.GameModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun infoRow(gm : GameModel, push : it.polito.did.compose.Components.pushResult){

    val timeCounter = gm.playerTimerCountdown.observeAsState()
    val stats = gm.teamsStats.observeAsState()
    val ableToPlay = gm.ableToPLay.observeAsState()

    var infoLayoutDefault by remember {
        mutableStateOf(true)
    }

    if (ableToPlay.value!! && timeCounter.value!! > 60){
        infoLayoutDefault = false
    }

    when {
        push == pushResult.CardDown || push == pushResult.Success -> {
            
            AnimatedContent(targetState = infoLayoutDefault, modifier = Modifier.fillMaxSize(),
            transitionSpec = { slideInHorizontally (tween(500)) { -it } with slideOutHorizontally (tween(500)) { it }})
            { infoLayout ->
                if (infoLayout){
                    Row(modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Column(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                            Text(text = gm.team)
                        }

                        Column(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            Text(text = "B: ${stats.value?.get(gm.team)?.budget}")
                        }

                        Column(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            if (timeCounter.value==null){
                                //todo : animazione di attesa turno del timer (tipo loading)
                            }
                            else {
                                AnimatedContent(targetState = timeCounter.value!!,
                                    transitionSpec = { infoRowTimeCounterAnimation().using(sizeTransform = SizeTransform(clip = true)) } ) {
                                        targetCounter ->
                                    Text(text = if (targetCounter < 10) "0${targetCounter}" else "${targetCounter}")
                                }
                            }
                        }
                    }
                }
                else{
                    LaunchedEffect(key1 = Unit, block = {
                        Log.d("inside launched effect", "")
                        delay(2200)
                        infoLayoutDefault = true
                    })
                    Row(modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Text(text = "your turn", color = Color.Green)
                    }
                }
            }
        }

        push == pushResult.InvalidCard -> {
            LaunchedEffect(key1 = Unit, block = {
                delay (2000)
                gm.pushResult.value = pushResult.CardDown
            })
            Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(text = " carta non valida", color = Color.Red)
        }}
        push == pushResult.LowBudget ->{
            LaunchedEffect(key1 = Unit, block = {
            delay (2000)
            gm.pushResult.value = pushResult.CardDown
            })
            Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(text = "budget esaurito", color = Color.Red)
        }}
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun infoRowTimeCounterAnimation (duration : Int = 500) : ContentTransform{
    return slideInVertically (animationSpec = tween(duration)) {it -> it} + fadeIn(animationSpec = tween(duration)) with
            slideOutVertically (animationSpec = tween(duration)){ it -> -it } + fadeOut(animationSpec = tween(duration))
}