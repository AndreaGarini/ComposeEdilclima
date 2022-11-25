package it.polito.did.compose.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.R
import it.polito.did.compose.ui.theme.GameTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun undetailedCard(crd : Card, motionSceneCode : Int) {

        androidx.compose.material3.Card(modifier = Modifier.layoutId("card$motionSceneCode")){

            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),   verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Text(text = crd.code)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),   verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Image(painter = painterResource(id = R.drawable.ic_android_black_24dp),
                        contentDescription = "card image", modifier = Modifier.fillMaxSize())
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),   verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround) {

                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),   verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround) {
                            Text(text = "cost : ")
                            Text(text = crd.money.toString())
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),   verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround) {
                            Text(text = "smog : ")
                            Text(text = crd.smog.toString())
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),   verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround) {
                            Text(text = "energy : ")
                            Text(text = crd.energy.toString())
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),   verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround) {
                            Text(text = "comfort : ")
                            Text(text = crd.comfort.toString())
                        }
                    }

                }
            }
        }
}