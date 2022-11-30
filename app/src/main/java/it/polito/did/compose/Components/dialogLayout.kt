package it.polito.did.compose.Components

import android.media.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.did.compose.DataClasses.dialogData
import it.polito.did.compose.GameModel
import kotlinx.coroutines.delay

@Composable
fun dialogLayout(data: dialogData, gm: GameModel) {

    if (data.timed) {
        LaunchedEffect(key1 = Unit, block = {
            delay(2000)
            gm.showDialog.value = null
        })
    }
    AlertDialog(
        onDismissRequest = {
            if (data.timed) gm.showDialog.value = null
        },
        title = {
            if (data.title != null) Text(text = data.title)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = data.text) //todo: qui aggiungi il brush del text
                }
                if (data.image != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                    }
                }
            }
        },
        buttons = {
            if (!data.timed) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { gm.showDialog.value = null }) {
                        Text(text = data.buttonText)
                    }
                }
            }
        },
        shape = RoundedCornerShape(10.dp)
    )
}