package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import it.polito.did.compose.GameModel
import java.math.MathContext
import java.math.RoundingMode

@Composable
fun gameBoardInfoCircle(gm : GameModel, maxWidth : Dp, maxHeight : Dp){

    val ongoingLevel = gm.ongoingLevel.observeAsState()
    val levelTimer = gm.levelTimerCountdown.observeAsState()

    if (ongoingLevel.value!!){
        Text(text = timeFormatMinSec((levelTimer.value!!).toFloat()))
        //todo: aggiungere curva timer centrale per il livello (da animare)
    }
    else{
            Button(onClick = { gm.startLevel(gm.gameLogic.level) }, shape = CircleShape,
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max)) {
                Text(text = "start level ${gm.gameLogic.level}")
            }
    }
}

fun timeFormatMinSec(levelTimer : Float?) : String{
    if (levelTimer!=null){
        val minutes : Int = (levelTimer/60f).toBigDecimal().round(MathContext(1, RoundingMode.DOWN)).toInt()
        val seconds : Int = (levelTimer%60f).toInt()

        if (seconds<10) return "${minutes}:0${seconds}"
        else  return "${minutes}:${seconds}"
    }
    else return "null"
}