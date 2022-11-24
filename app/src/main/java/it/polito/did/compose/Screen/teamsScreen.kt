package it.polito.did.compose.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import it.polito.did.compose.Components.teamInfoCard
import it.polito.did.compose.GameModel

@Composable
fun teamsInfoScreen(navController: NavController, portrait: Boolean, gm : GameModel, maxWidth : Dp, maxHeight : Dp){
    
    val teamsInfo = gm.teamsStats.observeAsState()
    
    LazyColumn(modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally){
        items(teamsInfo.value!!.size, {a -> a}, {c -> null},
            { b ->
                Row(modifier = Modifier.fillMaxWidth().height(maxHeight.times(0.33f))
                    .padding(horizontal = maxWidth.times(0.04f), vertical = maxHeight.times(0.05f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,) {
                    teamInfoCard(entry = teamsInfo.value!!.entries.toList()[b])
                }
            })
    }
}