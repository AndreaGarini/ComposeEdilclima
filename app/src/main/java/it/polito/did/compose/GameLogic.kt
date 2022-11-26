package it.polito.did.compose

import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.DataClasses.Zone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.stream.Collectors
import kotlin.streams.toList

class GameLogic(scope: CoroutineScope) {

    //todo : aggiungere tutta la logica per il calcolo dei punti
    //todo: definire un comportamento che ti impedisca di giocare se una ricerca è needed e non l'hai giocata
    //todo: implementare il timer di livello

    val firebaseAuth = Firebase.auth

    var level : Int = 0

    var playersPerTeam : Map<String, List<String>> = mutableMapOf()

    val months: List<String > = listOf("gen", "feb", "mar", "apr", "mag", "giu", "lug", "ago", "set", "ott", "nov", "dic")

    //todo : aggiungi la logica per poter giocare in meno di 4, per ora hai 4 team di default
    val zoneMap: Map<Int, Zone> = mapOf(
        1 to Zone(1, 50, 190, 280, 350, 200, 80, 50,
            listOf("H01", "H02", "H04", "H06", "E10", "E11", "E13", "E07", "E12", "A08", "A09","A12" ),
            listOf("H01", "E04", "A04", "E07", "no card")
        ))
    val cardsList : List<Card> = listOf(
        //ministero ambiente
        Card("A01", -80, 20, -30, 30, Card.researchSet.Needed, 2 ),
                Card("A02", -120, 0, -30, 0, Card.researchSet.Needed,2),
                Card("A03", -40, 0, -15, 25, Card.researchSet.Needed,2  ),
                Card("A04", -50, 0, -20, 15, Card.researchSet.None,1  ),
                Card("A05", -60, 0, -30, 25, Card.researchSet.None,2  ),
                Card("A06", -30, 0, -25, 20, Card.researchSet.None,1   ),
                Card("A07", -50, 0, -25, 20, Card.researchSet.Needed,2 ),
                Card("A08", 0, 0, -20, 20, Card.researchSet.None,1   ),
                Card("A09", -40, -20, -30, 15, Card.researchSet.None,1  ),
                Card("A10", -30, 0, -20, 0, Card.researchSet.None,1,    ),
                Card("A11", -50, 0, -25, 20, Card.researchSet.Needed,1,  ),
                Card("A12", 0, 25, 20, -30, Card.researchSet.None,1   ),
                Card("A13", 0, 20, -20, -30, Card.researchSet.None,2,   ),
                Card("A14", 0, 30, -30, 10, Card.researchSet.None,2,    ),
                Card("A15", -40, 0, -30, 30, Card.researchSet.Needed,1, ),
                Card("A16", -30, 0, -40, -30, Card.researchSet.None,2,   ),
                Card("A17", -20, 0, -10, 10, Card.researchSet.Needed,2, ),
                Card("A18", -30, 0, -15, 20, Card.researchSet.None,1,   ),
                Card("A19", -70, 0, -30, 40, Card.researchSet.None,2,  ),
                Card("A20", 0, 0, -25, -45, Card.researchSet.None,1,   ),

                //ministero energia
                Card("E01", -20, 30, 40, -20, Card.researchSet.None,2),
                Card("E02", -40, 50, 30, -10, Card.researchSet.None,  2),
                Card("E03", -70, 70, 10, -20, Card.researchSet.Needed, 2),
                Card("E04", -15, 10, 0, 10, Card.researchSet.None,  1),
                Card("E05", -10, 10, 0, 10, Card.researchSet.None, 2),
                Card("E06", -20, 10, 10, 10, Card.researchSet.Needed, 1),
                Card("E07", -30, 20, 10, 20, Card.researchSet.Needed, 1),
                Card("E08", -20, 10, -20, -20, Card.researchSet.None, 2),
                Card("E09", -10, 10, 0, 10, Card.researchSet.Needed, 1),
                Card("E10", 0, 15, 10, 20, Card.researchSet.Needed, 1),
                Card("E11", 0, 15, 0, 20, Card.researchSet.Needed, 1),
                Card("E12", 0, 10, -25, 10, Card.researchSet.Needed, 1),
                Card("E13", 0, 10, 0, 20, Card.researchSet.Needed, 1),
                Card("E14", 0, 20, 0, 10, Card.researchSet.Needed, 2),
                Card("E15", -10, 20, 10, 35, Card.researchSet.None,  1),
                Card("E16", -40, 50, 10, -50, Card.researchSet.None,  2),
                Card("E17", -50, 40, 0, 0, Card.researchSet.None,  1),
                Card("E18", -200, 150, 0, 50, Card.researchSet.Needed, 2),
                //ministero HR
                Card("H01", -40, 10, 0, 20, Card.researchSet.Develop,1),
                Card("H02", -80, 0, 0, 10, Card.researchSet.Develop,  1),
                Card("H03", -20, 0, 0, 0, Card.researchSet.Develop, 1),
                Card("H04", -50, 10, -10, 20, Card.researchSet.Develop,  1),
                Card("H05", -60, 0, 0, 20, Card.researchSet.Develop, 1),
                Card("H06", -60, 10, 0, 20, Card.researchSet.Develop, 1),
                Card("H07", -20, 0, -10, 10, Card.researchSet.Develop, 1),
                Card("H08", -30, 0, 0, 0, Card.researchSet.Develop, 2),
                Card("H09", -30, 0, -10, 10, Card.researchSet.Develop, 2),
                //mosse.add(Mossa("H10", 0, 15, 10, 20, Mossa.researchSet.Develop, 1));
                //mosse.add(Mossa("H11", 0, 15, 0, -20, Mossa.researchSet.Develop, 1));
                Card("H12", -40, 0, -10, 20, Card.researchSet.Develop, 2),
                Card("H13", -40, 10, 0, 20, Card.researchSet.Develop, 2)
    )
    val cardsMap : Map<String, Card> = cardsList.stream().collect(Collectors.toMap({ a -> a.code}, {b -> b}))

    init {
        scope.launch {
            try {
                //todo: aggiungi una coroutine per attendere l'autenticazione
                firebaseAuth.signInAnonymously()
                Log.d("GameManager", "Current User: ${firebaseAuth.uid}")
                delay(500)
            } catch (e: Exception) {

            }
        }
    }

    fun setPlayerTimer(timeToFinish : Long, tickInterval : Long, onTick: () -> Unit, onFinish : () -> Unit) {
        val timer: CountDownTimer = object :  CountDownTimer(timeToFinish, tickInterval) {
            override fun onTick(millisUntilFinished: Long) {
                    onTick()
            }

            override fun onFinish() {
                    onFinish()
            }
        }
        timer.start()
    }

    fun setLevelTimer (onTick: () -> Unit, onFinish : () -> Unit) {
        val timer: CountDownTimer = object :  CountDownTimer(420000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick()
            }

            override fun onFinish() {
                onFinish()
            }
        }
        timer.start()
    }



    fun selectTeamForPlayers (snapshot: DataSnapshot) : MutableMap<String, Map<String, String>> {
        val resultingMap : MutableMap<String, Map<String, String>> = mutableMapOf()
        var counter : Int = 0
        val teams: List<String> = listOf("team1", "team2", "team3", "team4")
        for (player in snapshot.children){
            resultingMap.put(player.key!!,
            mapOf("team" to teams[counter%4], "ownedCards" to ""))
            counter++
        }
        playersPerTeam = resultingMap.mapValues { a -> a.value.get("team") as String}.entries.groupBy { b -> b.value }
            .mapValues { c -> c.value.stream().map { d -> d.key }.toList() }
        println(playersPerTeam.toString())

        return resultingMap
    }

    fun createTeamsOnDb() : MutableMap<String, Map<String, Any>>{
        val resultingMap : MutableMap<String, Map<String, Any>> = mutableMapOf()
        val teams: List<String> = listOf("team1", "team2", "team3", "team4")
        for (team in teams){
            resultingMap.put(team, mapOf("ableToPlay" to "", "playedCards" to "", "points" to 0))
        }

        return resultingMap
    }

    fun cardsToPlayers(level : Int) : Map<String, Map<String, Boolean>>{

        val resultingMap : MutableMap<String, Map<String, Boolean>> = mutableMapOf()

        val startingSetCards: List<String> = cardsList.filter { a -> (!zoneMap.get(level)!!.startingList!!.contains(a.code) && a.level==level) }
            .map { b -> b.code }

        for(entry in playersPerTeam){
            var counterCrds : Int = 0
            var avatarMap : MutableMap<String, MutableList<String>> = mutableMapOf()

            for (player in entry.value){
                avatarMap.put(player, mutableListOf())
            }
            while (counterCrds < startingSetCards.size ){
                var playersNum : Int = (counterCrds%entry.value.size)
                avatarMap.get(avatarMap.keys.toList()[playersNum])!!.add(startingSetCards[counterCrds])
                counterCrds++
            }
            avatarMap.entries.forEach { a -> a.value.add("void") }
            resultingMap.putAll( avatarMap.mapValues { a -> a.value.
            stream().collect(Collectors.toMap({ b -> b }, {c -> true})) })
        }

        return resultingMap

    }

    fun findNextPlayer(team : String, lastPlayer: String) : String{

       var oldIndex = if(lastPlayer.equals("")) -1 else playersPerTeam.get(team)!!.indexOf(lastPlayer)
       if (oldIndex == playersPerTeam.get(team)!!.size) oldIndex = -1

        return playersPerTeam.get(team)!![oldIndex + 1]
    }

    fun nextLevel() : Int{
        level++
        return level
    }

    fun findCard(cardCode : String) : Card?{
       return cardsMap.get(cardCode)
    }

    //funzioni per calcolare i dati di squadra in un livello


}
