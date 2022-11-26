package it.polito.did.compose.Screen

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import it.polito.did.compose.Components.gameBoardCard
import it.polito.did.compose.Components.gameBoardInfoCircle
import it.polito.did.compose.GameModel
import java.math.MathContext
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun gameBoardScreen(navController: NavController, portraitOrientation : Boolean , gm : GameModel, maxWidth : Dp, maxHeight: Dp) {

    val playedCardsPerTeam = gm.playedCardsPerTeam.observeAsState()

    val teamsCount: Int = playedCardsPerTeam.value?.keys!!.count()
    val rowsCount: Int =
        ((teamsCount / 2).toBigDecimal().round(MathContext(1, RoundingMode.DOWN)).toInt())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            for (teamsRowIndex in 0 until rowsCount) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (teamsRowIndex == (rowsCount - 1) && (teamsCount % 2 == 1)) {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(
                                    horizontal = maxWidth.times(0.01f),
                                    vertical = maxHeight.times(0.01f)
                                )
                        ) {
                            gameBoardCard()
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(
                                    horizontal = maxWidth.times(0.01f),
                                    vertical = maxHeight.times(0.01f)
                                )
                        ) {
                            gameBoardCard()
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(
                                    horizontal = maxWidth.times(0.01f),
                                    vertical = maxHeight.times(0.01f)
                                )
                        ) {
                            gameBoardCard()
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .width(maxWidth.times(0.11f))
                    .height(maxWidth.times(0.11f)).clip(CircleShape)
                    .background(Color.White),
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                        gameBoardInfoCircle(gm = gm, maxWidth = maxWidth, maxHeight = maxHeight)
                    }
                }
        }
    }
}


