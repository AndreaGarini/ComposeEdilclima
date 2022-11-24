package it.polito.did.compose.Components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.GameModel
import it.polito.did.compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detailedCard( card: Card?, gm: GameModel, detailedCardModifier : Modifier){

     val playedCards = gm.playedCardsPerTeam.observeAsState()

    if (card != null){
        Card(modifier = detailedCardModifier) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally) {
                //nome carta
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                        Text(text = card.code)
                }
                //image
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Image(painter = painterResource(id = R.drawable.ic_android_black_24dp),
                        modifier = Modifier.fillMaxSize(), contentDescription = "detailedCard")
                }
                //costo carta
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                       Text(text = "cost : ${card.money}")
                }
                //dati carta
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                     Column(modifier = Modifier
                         .fillMaxHeight()
                         .weight(1f),
                         verticalArrangement = Arrangement.SpaceAround,
                         horizontalAlignment = Alignment.CenterHorizontally) {
                         Text(text = "smog : ${card.smog}")
                     }
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "energy : ${card.energy}")
                    }
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "comfort : ${card.comfort}")
                    }
                }
                //testo della carta
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                     Text(text = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore " +
                             "et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex " +
                             "ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat " +
                             "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                }
            }
        }
    }
    
    else{
        androidx.compose.material3.Card(modifier = detailedCardModifier) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                    Text(text = "no card")
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Image(painter = painterResource(id = R.drawable.ic_android_black_24dp),
                        modifier = Modifier.fillMaxSize(), contentDescription = "detailedCard")
                }
            }
        }
    }
    
}