package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import it.polito.did.compose.DataClasses.GreenHSV
import it.polito.did.compose.DataClasses.RedHSV
import it.polito.did.compose.GameModel
import java.math.MathContext
import java.math.RoundingMode

@Composable
fun gameBoardInfoCircle(gm : GameModel, circleWidth : Dp, circleHeight : Dp){

    val ongoingLevel = gm.ongoingLevel.observeAsState()
    val levelTimer = gm.levelTimerCountdown.observeAsState()
    val levelStatus = gm.masterLevelStatus.observeAsState()

    if (ongoingLevel.value!!){
        val arcEndingAngle = (360f * ((levelTimer.value!!).toFloat()/420f))
        val colorValueList = generateColor(levelTimer.value!!.toFloat())
        val arcColor = Color.hsv(colorValueList[0], colorValueList[1], colorValueList[2])
        Canvas(modifier = Modifier.size(circleWidth)){
            drawArc(color = arcColor, -180f, arcEndingAngle, false,
                style = Stroke(width = circleWidth.times(0.05f).toPx(), cap = StrokeCap.Round))
        }
        Text(text = timeFormatMinSec((levelTimer.value!!).toFloat()))
    }
    else{
            Button(onClick = { if (levelStatus.value!!.equals("preparing")) gm.prepareLevel(gm.gameLogic.masterLevelCounter) else {gm.startLevel() } }, shape = CircleShape,
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max)) {
                Text(text =if (levelStatus.value!!.equals("preparing")) "prepare level ${gm.gameLogic.masterLevelCounter}" else "start level ${gm.gameLogic.masterLevelCounter}")
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

fun generateColor (levelTimer : Float) : List<Float>{
    val hueRange : Int = GreenHSV.hue - RedHSV.hue
    val satRange : Int = GreenHSV.sat - RedHSV.sat
    val valueRange : Int = GreenHSV.valu - RedHSV.valu

    return listOf(RedHSV.hue + (hueRange * (levelTimer/420f)),
        (RedHSV.sat + (satRange * (levelTimer/420f)))/100f,
        (RedHSV.valu + (valueRange * (levelTimer/420f)))/100f)
}