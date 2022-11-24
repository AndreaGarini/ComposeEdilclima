package it.polito.did.compose.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument

@Composable
 fun bottomBar (navController : NavController) {

     NavigationBar(modifier = Modifier.fillMaxWidth()) {

         val navBackStackEntry by navController.currentBackStackEntryAsState()
         val currentRoute = navBackStackEntry?.destination?.route

         NavBarItems.itemsList.forEach {
             NavigationBarItem( selected = currentRoute == it.route,
                                onClick = { navController.navigate(it.route) },
                                icon = {
                                     Icon(painter = painterResource(id = it.icon), contentDescription = it.text)
                                 },
                                label = {Text(text = it.text)})
                             }
     }


}

data class barItem (val icon: Int, val text: String, val route: String)



object NavBarItems{
    val itemsList : List<barItem> = listOf(
        barItem( it.polito.did.compose.R.drawable.play_card, "Gioca carta", "cardSelectionScreen"),
        barItem(it.polito.did.compose.R.drawable.retrive_card, "Prendi carta", "retriveCardScreen"),
        barItem(it.polito.did.compose.R.drawable.other_teams, "squadre", "teamsInfoScreen")

    )
}