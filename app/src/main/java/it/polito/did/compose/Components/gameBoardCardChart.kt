package it.polito.did.compose.Components

import android.os.CountDownTimer
import android.util.Log
import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.polito.did.compose.DataClasses.BlueHSV
import it.polito.did.compose.DataClasses.GreenHSV
import it.polito.did.compose.DataClasses.RedHSV
import it.polito.did.compose.DataClasses.TeamInfo
import it.polito.did.compose.GameModel
import it.polito.did.compose.ui.theme.GameTheme
import java.lang.Math.abs

@Composable
fun gameBoardCardChart(gm : GameModel, team : String){

    val teamsInfo = gm.teamsStats.observeAsState()

    val teamInfoMap : Map<String, Float> = generateTeamInfoMap(teamsInfo.value!!, team, gm)

    var animateChartBars by remember {
        mutableStateOf(false)
    }

    if (!animateChartBars) {
        LaunchedEffect(key1 = Unit, block = {animateChartBars = true})
    }

    Column() {

        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f), verticalAlignment = Alignment.CenterVertically
        , horizontalArrangement = Arrangement.Center) {
            Text(text = team)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f),
            verticalAlignment = Alignment.Bottom
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f / teamInfoMap.size + 1),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "100%", textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(7f))
                Text(
                    text = "0%", textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(2f))
            }

            teamInfoMap.entries.forEach {
                val height = animateFloatAsState(
                    targetValue = if (animateChartBars) (it.value / 100f) else 0f,
                    animationSpec = tween(2000)
                )
                val colorValueList = generateColorChart(it.value, it.key)
                val arcColor =
                    Color.hsv(colorValueList[0], colorValueList[1], colorValueList[2])
                val color = animateColorAsState(
                    targetValue = if (animateChartBars) arcColor else Color.Transparent,
                    animationSpec = tween(2000)
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f / teamInfoMap.size + 1),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(modifier = Modifier.fillMaxWidth(0.5f), thickness = 4.dp, color = color.value)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(7f), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(fraction = height.value)
                                .fillMaxWidth(fraction = 0.5f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = color.value)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = it.key)
                    }
                }
            }
        }
    }
}

fun generateTeamInfoMap (teamsInfo : Map<String, TeamInfo?>, team : String, gm : GameModel) : Map<String, Float>{

        val smog = teamsInfo.get(team)!!.smog
        val energy = teamsInfo.get(team)!!.energy
        val comfort = teamsInfo.get(team)!!.comfort

        if (teamsInfo.get(team)!!.nullCheck()) {

            val smogRatio =
                abs(smog!!.minus(gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initSmog)).toFloat() /
                        abs(
                            gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initSmog - gm.gameLogic.zoneMap.get(
                                gm.gameLogic.masterLevelCounter
                            )!!.TargetA
                        ).toFloat()

            val energyRatio =
                abs(energy!!.minus(gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initEnergy)).toFloat() /
                        abs(
                            gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initEnergy - gm.gameLogic.zoneMap.get(
                                gm.gameLogic.masterLevelCounter
                            )!!.TargetE
                        ).toFloat()
            val comfortRatio =
                abs(comfort!!.minus(gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initComfort)).toFloat() /
                        abs(
                            gm.gameLogic.zoneMap.get(gm.gameLogic.masterLevelCounter)!!.initComfort - gm.gameLogic.zoneMap.get(
                                gm.gameLogic.masterLevelCounter
                            )!!.TargetC
                        ).toFloat()

            return mapOf(
                "S" to smogRatio * 100f,
                "E" to energyRatio * 100f,
                "C" to comfortRatio * 100f
            )
        }

    else return mapOf("S" to 0f, "E" to 0f, "C" to 0f)

}

fun generateColorChart (value : Float, dataKey : String) : List<Float>{

    val percentage : Float = value /200f

    when (dataKey){
        "S" -> {
            return listOf(GreenHSV.hue.toFloat(), GreenHSV.sat.toFloat()/100f, (GreenHSV.valu.times(0.5f) + GreenHSV.valu.times(percentage))/105f)
        }
        "E" -> {
            return listOf(RedHSV.hue.toFloat(), RedHSV.sat.toFloat()/100f, (RedHSV.valu.times(0.5f) + RedHSV.valu.times(percentage))/105f)
        }
        "C" -> {
            return listOf(BlueHSV.hue.toFloat(), BlueHSV.sat.toFloat()/100f, (BlueHSV.valu.times(0.5f) + BlueHSV.valu.times(percentage))/105f)
        }
        else -> return listOf(0f, 0f, 0f)
    }
}