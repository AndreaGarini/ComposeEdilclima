package it.polito.did.compose.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material3.Button
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import it.polito.did.compose.Components.detailedCard
import it.polito.did.compose.Components.infoRow
import it.polito.did.compose.Components.pagerCard
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.GameModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun retriveCardScreen(navController: NavController, portrait : Boolean, gm: GameModel, usableWidth: Dp, usableHeight: Dp){

    //todo: le carte vanno rese scrollable perchè il testo non è detto che ci stia
    //todo : aggiungi un'animazione per il retrive card
    val pagerState = rememberPagerState()

    val playedCards = gm.playedCardsPerTeam.observeAsState()

    val ableToPlay = gm.playerTimer.observeAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .background(Color.Transparent)
                )
            },
            backgroundColor = Color.Transparent,
            modifier = Modifier.weight(1f)

        ) {
            // Add tabs for all of our pages
            gm.gameLogic.months.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.width(usableWidth.times(0.25f)),
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    },
                )
            }
        }

        HorizontalPager(
            count = gm.gameLogic.months.size,
            state = pagerState,
            modifier = Modifier.weight(9f)
        ) { index ->
                    val detailedCardModifier = Modifier.
                    graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(index).absoluteValue

                        scaleX = (1f - (pageOffset * 0.3f))
                        scaleY = (1f - (pageOffset * 0.3f))
                        alpha = 1 - pageOffset
                    }
            Column(modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = usableWidth.times(0.12f))) {
                val cardCode : String? = playedCards.value!!.get(gm.team)?.get(gm.gameLogic.months[index])
                detailedCard(gm.gameLogic.cardsMap[cardCode], gm, detailedCardModifier)
            }
        }
        
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalAlignment = Alignment.CenterVertically, 
        horizontalArrangement = Arrangement.Center) {
            // todo: button enabled false se non c'è una carta
            Button(onClick = { gm.retriveCardFromPos(pagerState.currentPage) },
            enabled = (playedCards.value?.get(gm.team)?.get(gm.gameLogic.months[pagerState.currentPage]) != null) && ableToPlay.value!=null) {
                Text(text = "retrive card")
            }
        }
    }
}