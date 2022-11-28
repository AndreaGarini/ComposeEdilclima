package it.polito.did.compose.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.math.MathUtils
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import it.polito.did.compose.GameModel
import it.polito.did.compose.ui.theme.GameTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun cardCarousel(gm :GameModel, cardPlayable : String, animateToStart: () -> Unit, usableWidth : Dp){

      val pagerState = rememberPagerState()
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
                      onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                           },
                  )
              }
          }

          HorizontalPager(
              count = gm.gameLogic.months.size,
              state = pagerState,
              modifier = Modifier.weight(2.5f)
          ) { index ->
              val pagerCardModifier = Modifier.fillMaxSize().
                  graphicsLayer {
                      val pageOffset = calculateCurrentOffsetForPage(index).absoluteValue

                      scaleX = (1f - (pageOffset * 0.3f))
                      scaleY = (1f - (pageOffset * 0.3f))
                      alpha = 1 - pageOffset
                  }
              pagerCard(index, gm, cardPlayable, animateToStart = animateToStart, pagerCardModifier)
          }
      }
}