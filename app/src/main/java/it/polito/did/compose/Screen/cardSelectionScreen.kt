package it.polito.did.compose.Screen

import android.util.Log
import android.view.View
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import it.polito.did.compose.Components.*
import it.polito.did.compose.Components.pushResult
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.DataClasses.researchSet
import it.polito.did.compose.GameModel
import it.polito.did.compose.R
import it.polito.did.compose.ui.theme.GameTheme
import java.lang.Math.abs
import java.lang.Math.log


@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
@Composable
fun cardSelectionScreen(navController: NavController?, portrait: Boolean, gm: GameModel, usableWidth : Dp, usableHeight : Dp) {

    val context = LocalContext.current
    var motionScene by remember {
       mutableStateOf( context.resources.openRawResource(R.raw.motion_scene).readBytes().decodeToString())
    }
    
    var direction by remember {
        mutableStateOf(it.polito.did.compose.Screen.Direction.None)
    }

    var counter by remember {
        mutableStateOf(1)
    }

    var cardPlayable by remember {
       mutableStateOf("null")
    }

    val ableToPlay = gm.playerTimer.observeAsState()
    val playerCards = gm.playerCards.observeAsState()

    val dragModifier : Modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()

                val (x, y) = dragAmount
                if (abs(x) > abs(y)) {
                    when {
                        (x > 0 && direction != Direction.Up) -> direction = Direction.Right
                        (x < 0 && direction != Direction.Up) -> direction = Direction.Left
                    }
                } else {
                    when {
                        (y > 0 && direction == Direction.Up) -> {
                            direction = Direction.Down
                            cardPlayable = "null"
                        }
                        (y < 0 && ableToPlay.value!=null) -> direction = Direction.Up
                    }
                }
            }
        }

    val animateToStart = {
        direction = Direction.Down
        cardPlayable = "null"
    }

    //condizione per far tornare la carta Down se si finisce il tempo con la carta in Up
    if(ableToPlay.value==null && direction == Direction.Up){
        animateToStart()
    }

    if (playerCards.value != null && playerCards.value!!.size < 7 ){
        for (i in playerCards.value!!.size until 7){
            playerCards.value!!.add(i, Card("void", 0, 0, 0, 0, researchSet.None, null,1),)
        }
    }

    //todo : sistema l'animazione di Up in modo che resti solo la carta centrale
    //todo: togli il null check da qui e metti la schermata di splash prima (se il giocatore va a zero carte deve poter comunque vedere questa schermata)

    if (playerCards.value != null && (playerCards.value!!.size > 0)) {
        //todo: sistemare il binding che salta

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            motionScene = motionScene.replace("'MinWidth'", (usableWidth.times(0.3F)).toString().take(3))
            .replace("'MinHeight'", (usableHeight.times(0.3F)).toString().take(3))
            .replace("'MidWidth'", (usableWidth.times(0.4F)).toString().take(3))
            .replace("'MidHeight'", (usableHeight.times(0.4F)).toString().take(3))
            .replace("'TopWidth'", (usableWidth.times(0.5F)).toString().take(3))
            .replace("'TopHeight'", (usableHeight.times(0.5F)).toString().take(3))
                .replace("'centralDist'", (usableHeight.times(0.3F).toString().take(3)))
                .replace("'firstDist'", (usableHeight.times(0.3F).toString().take(3)))
                .replace("'secondDist'", (usableHeight.times(0.3F).toString().take(3)))
                .replace("'thirdDist'", (usableHeight.times(0.25F).toString().take(3)))
                .replace("'centralUpDist'", (usableHeight.times(0.45F).toString().take(3)))
                .replace("'firstUpDist'", (usableHeight.times(0.1F).toString().take(3)))
                .replace("'secondUpDist'", (usableHeight.times(0.05F).toString().take(3)))

            Column(modifier = dragModifier) {
                
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)){
                    cardCarousel(gm = gm, cardPlayable, animateToStart, usableWidth)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(6f)) {

                        when {
                            direction == Direction.None -> {
                                MotionLayout(
                                    motionScene = MotionScene(content = motionScene),
                                    constraintSetName = "start",
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    Spacer(modifier = Modifier.layoutId("guide"))
                                    Log.d("playerCards size in motionLayout: ", playerCards.value!!.size.toString())
                                    for (i in 0 until 7) {
                                        undetailedCard(
                                            crd = playerCards.value!![(i + counter) % (playerCards.value!!.size)],
                                            motionSceneCode = i,
                                        )
                                    }
                                }
                            }

                            direction == Direction.Right -> {
                                MotionLayout(
                                    motionScene = MotionScene(content = motionScene),
                                    constraintSetName = "shiftRight",
                                    animationSpec = tween(
                                        1000,
                                        easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
                                    ),
                                    finishedAnimationListener = {
                                        if (counter == 0) counter = playerCards.value!!.size
                                        else counter--

                                        direction = Direction.None
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    Spacer(modifier = Modifier.layoutId("guide"))
                                    for (i in 0 until 7) {
                                        undetailedCard(
                                            crd = playerCards.value!![(i + counter) % playerCards.value!!.size],
                                            motionSceneCode = i,
                                        )
                                    }
                                }
                            }
                            direction == Direction.Left -> {
                                MotionLayout(
                                    motionScene = MotionScene(content = motionScene),
                                    constraintSetName = "shiftLeft",
                                    animationSpec = tween(
                                        1000,
                                        easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
                                    ),
                                    finishedAnimationListener = {
                                        counter++

                                        direction = Direction.None
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    Spacer(modifier = Modifier.layoutId("guide"))
                                    for (i in 0 until 7) {
                                        undetailedCard(
                                            crd = playerCards.value!![(i + counter) % playerCards.value!!.size],
                                            motionSceneCode = i,
                                        )
                                    }
                                }
                            }
                            direction == Direction.Up -> {
                                MotionLayout(
                                    motionScene = MotionScene(content = motionScene),
                                    constraintSetName = "shiftUp",
                                    animationSpec = tween(
                                        1000,
                                        easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
                                    ),
                                    finishedAnimationListener = {
                                        cardPlayable =
                                            playerCards.value!![(3 + counter) % playerCards.value!!.size].code
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    Spacer(modifier = Modifier.layoutId("guide"))
                                    for (i in 0 until 7) {
                                        undetailedCard(
                                            crd = playerCards.value!![(i + counter) % playerCards.value!!.size],
                                            motionSceneCode = i,
                                        )
                                    }
                                }
                            }
                            direction == Direction.Down -> {
                                Log.d("in down", "")
                                //todo : scoprire perchè non anima qui
                                MotionLayout(
                                    motionScene = MotionScene(content = motionScene),
                                    constraintSetName = "start",
                                    animationSpec = tween(
                                        1000,
                                        easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
                                    ),
                                    finishedAnimationListener = {
                                        Log.d("finished animation listener in direction down", "")
                                        direction = Direction.None
                                        cardPlayable = "null"
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()

                                ) {
                                    Spacer(modifier = Modifier.layoutId("guide"))
                                    for (i in 0 until 7) {
                                        undetailedCard(
                                            crd = playerCards.value!![(i + counter) % playerCards.value!!.size],
                                            motionSceneCode = i,
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
    else{
        Log.d("no value", "")
        Column(modifier = Modifier.fillMaxSize()) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "no value")
            }
        }
    }
}

enum class Direction {
Right, Left, Up, Down, None
}

data class screenState( var cardPlayable : String, var counter : Int, var direction : Direction) {
}