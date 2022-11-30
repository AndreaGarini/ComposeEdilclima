package it.polito.did.compose.Components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.did.compose.GameModel

@Composable
fun gameBoardCard(gm : GameModel, team : String){

     Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
          Row(modifier = Modifier
               .fillMaxWidth()
               .weight(3f), verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center) {
               gameBoardCardChart(gm = gm, team = team)
          }
          Divider(modifier = Modifier.fillMaxWidth(0.8f), thickness = 5.dp)
          Row(modifier = Modifier
               .fillMaxWidth()
               .weight(1f), verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center) {
               gameBoardCardsRow(gm = gm , team = team)
          }
     }
}