package it.polito.did.compose

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.did.compose.Screen.WaitingScreen
import it.polito.did.compose.ui.theme.GameTheme

class MainActivity : ComponentActivity() {

    lateinit var gameModel : GameModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameModel = ViewModelProvider(this).get(GameModel::class.java)

        setContent {
            Log.d("starting: ", "activity")
            GameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    ) {
                       if(constraints.maxHeight > constraints.maxWidth){
                           WaitingScreen(true, gameModel)
                       }
                        else {
                           WaitingScreen(false, gameModel)
                        }
                    }
                }
            }
        }
    }
}