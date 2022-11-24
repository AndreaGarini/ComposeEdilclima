package it.polito.did.compose.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import it.polito.did.compose.GameModel
import it.polito.did.compose.ui.theme.GameTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun cardCarousel(gm :GameModel, cardPlayable : String, animateToStart: () -> Unit){

      val pagerState = rememberPagerState()

      Column(modifier = Modifier.fillMaxSize()) {
          ScrollableTabRow(
              // Our selected tab is our current page
              selectedTabIndex = pagerState.currentPage,
              // Override the indicator, using the provided pagerTabIndicatorOffset modifier
              indicator = { tabPositions ->
                  TabRowDefaults.Indicator(
                      Modifier.pagerTabIndicatorOffset(pagerState, tabPositions).background(Color.Transparent)
                  )
              },
              backgroundColor = Color.Transparent,
              modifier = Modifier.weight(1f)

          ) {
              // Add tabs for all of our pages
              gm.gameLogic.months.forEachIndexed { index, title ->
                  Tab(
                      text = { Text(title) },
                      selected = pagerState.currentPage == index,
                      onClick = { //todo: utilizzare le coroutine per fare lo scrollToPage
                           },
                  )
              }
          }

          HorizontalPager(
              count = gm.gameLogic.months.size,
              state = pagerState,
              modifier = Modifier.weight(2.5f)
          ) { index ->
              pagerCard(index, gm, cardPlayable, animateToStart = animateToStart)
          }
      }
}