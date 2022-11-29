package it.polito.did.compose.Components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.polito.did.compose.GameModel

@Composable
fun gameBoardCard(gm : GameModel, team : String){

     gameBoardCardChart(gm = gm, team = team)
}