package it.polito.did.compose.Screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.did.compose.GameModel
import it.polito.did.compose.ui.theme.GameTheme

@Composable
fun initialScreen(navController: NavController?, portrait: Boolean, gm: GameModel, usableWidth : Dp, usableHeight : Dp) {

    //todo: aggiungi una password da inserire se si decide di creare un nuovo match
    BoxWithConstraints {

        if(portrait){
            Column(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    imageVector = ImageVector.vectorResource(it.polito.did.compose.R.drawable.ic_android_black_24dp),
                    contentDescription = "Splash",
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f),
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Button (onClick = {
                        navController?.navigate("matchMakingScreen")
                    }){
                        Text(text = "Start Match")
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Button (onClick = {
                        navController?.navigate("cameraScreen")
                    }){
                        Text(text = "Join Match")
                    }
                }
            }
        }

        else {
            Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
                Column(modifier = Modifier
                    .fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        imageVector = ImageVector.vectorResource(it.polito.did.compose.R.drawable.ic_android_black_24dp),
                        contentDescription = "Splash",
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                    )
                }
                Column(modifier = Modifier
                    .fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = {
                        navController?.navigate("matchMakingScreen")
                    }) {
                        Text(text = "Start Match")
                    }

                    Button(onClick = {
                        navController?.navigate("cameraScreen")
                    }) {
                        Text(text = "Join Match")
                    }
                }
            }

        }
    }
}