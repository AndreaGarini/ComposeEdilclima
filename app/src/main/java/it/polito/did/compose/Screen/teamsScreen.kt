package it.polito.did.compose.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.did.compose.Components.teamInfoCard
import it.polito.did.compose.GameModel

@Composable
fun teamsInfoScreen(navController: NavController, portrait: Boolean, gm : GameModel, maxWidth : Dp, maxHeight : Dp){
    
    val teamsInfo = gm.teamsStats.observeAsState()
    
    LazyColumn(modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally){

        //todo: in questa pagina aggiungere il fab per raggiungere la classifica generale
        items(teamsInfo.value!!.size, {a -> if(teamsInfo.value!!.keys.toList()[a]==gm.team) 0 else a + 1}, {c -> null},
            { b ->
                when {
                    teamsInfo.value!!.keys.toList()[b]==gm.team ->

                        Column(modifier = Modifier
                            .fillMaxWidth().width(IntrinsicSize.Max),
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        verticalArrangement = Arrangement.Center) {
                            
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,) {
                                Text(text = "your team", modifier = Modifier.padding(vertical = maxHeight.times(0.01f)))
                                Divider(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = maxWidth.times(0.04f)))
                            }
                            
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(maxHeight.times(0.33f))
                                .padding(
                                    horizontal = maxWidth.times(0.04f),
                                    vertical = maxHeight.times(0.05f)
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,) {
                                teamInfoCard(entry = teamsInfo.value!!.entries.toList()[b])
                            }

                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,) {
                                Spacer(modifier = Modifier.height(maxHeight.times(0.05f)))
                                Text(text = "other team", modifier = Modifier.padding(vertical = maxHeight.times(0.01f)))
                                Divider(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = maxWidth.times(0.04f)))
                            }
                        }
                    
                    else ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(maxHeight.times(0.33f))
                                    .padding(
                                        horizontal = maxWidth.times(0.04f),
                                        vertical = maxHeight.times(0.05f)
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,) {
                                    teamInfoCard(entry = teamsInfo.value!!.entries.toList()[b])
                                    }
                    
                }
            })
    }
}