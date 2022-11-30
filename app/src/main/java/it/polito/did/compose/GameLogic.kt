package it.polito.did.compose

import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.DataClasses.TeamInfo
import it.polito.did.compose.DataClasses.Zone
import it.polito.did.compose.DataClasses.researchSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.stream.Collectors
import kotlin.streams.toList

class GameLogic(scope: CoroutineScope) {

    val firebaseAuth = Firebase.auth

    var masterLevelCounter : Int = 0

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
        Card("A01", -80, 20, -30, 30, researchSet.Needed, listOf("H13"), 2 ),
                Card("A02", -120, 0, -30, 0, researchSet.Needed, listOf("H08"),2),
                Card("A03", -40, 0, -15, 25, researchSet.Needed, listOf("H09"), 2  ),
                Card("A04", -50, 0, -20, 15, researchSet.None, null,1  ),
                Card("A05", -60, 0, -30, 25, researchSet.None, null,2  ),
                Card("A06", -30, 0, -25, 20, researchSet.None, null,1   ),
                Card("A07", -50, 0, -25, 20, researchSet.Needed, listOf("H03"),2 ),
                Card("A08", 0, 0, -20, 20, researchSet.None, null,1   ),
                Card("A09", -40, -20, -30, 15, researchSet.None, null,1  ),
                Card("A10", -30, 0, -20, 0, researchSet.None, null,1,    ),
                Card("A11", -50, 0, -25, 20, researchSet.Needed, listOf("H05"),1,  ),
                Card("A12", 0, 25, 20, -30, researchSet.None, null,1   ),
                Card("A13", 0, 20, -20, -30, researchSet.None, null,2,   ),
                Card("A14", 0, 30, -30, 10, researchSet.None, null,2,    ),
                Card("A15", -40, 0, -30, 30, researchSet.Needed, listOf("H05", "H07"),1, ),
                Card("A16", -30, 0, -40, -30, researchSet.None, null,2,   ),
                Card("A17", -20, 0, -10, 10, researchSet.Needed, listOf("H12"),2, ),
                Card("A18", -30, 0, -15, 20, researchSet.None, null,1,   ),
                Card("A19", -70, 0, -30, 40, researchSet.None, null,2,  ),
                Card("A20", 0, 0, -25, -45, researchSet.None, null,1,   ),

                //ministero energia
                Card("E01", -20, 30, 40, -20, researchSet.None, null, 2),
                Card("E02", -40, 50, 30, -10, researchSet.None, null,  2),
                Card("E03", -70, 70, 10, -20, researchSet.Needed, listOf("H08"),2),
                Card("E04", -15, 10, 0, 10, researchSet.None, null,  1),
                Card("E05", -10, 10, 0, 10, researchSet.None, null, 2),
                Card("E06", -20, 10, 10, 10, researchSet.Needed, listOf("H03"),1),
                Card("E07", -30, 20, 10, 20, researchSet.Needed, listOf("H04"),1),
                Card("E08", -20, 10, -20, -20, researchSet.None, null,2),
                Card("E09", -10, 10, 0, 10, researchSet.Needed, listOf("H03", "H06"),1),
                Card("E10", 0, 15, 10, 20, researchSet.Needed, listOf("H06"), 1),
                Card("E11", 0, 15, 0, 20, researchSet.Needed, listOf("H01", "H02"),1),
                Card("E12", 0, 10, -25, 10, researchSet.Needed, listOf("H06"),1),
                Card("E13", 0, 10, 0, 20, researchSet.Needed, listOf("H01", "H02"),1),
                Card("E14", 0, 20, 0, 10, researchSet.Needed, listOf("H13"),2),
                Card("E15", -10, 20, 10, 35, researchSet.None, null,  1),
                Card("E16", -40, 50, 10, -50, researchSet.None, null,  2),
                Card("E17", -50, 40, 0, 0, researchSet.None,  null,1),
                Card("E18", -200, 150, 0, 50, researchSet.Needed, listOf("H08"), 2),
                //ministero HR
                Card("H01", -40, 10, 0, 20, researchSet.Develop, null,1),
                Card("H02", -80, 0, 0, 10, researchSet.Develop, null,  1),
                Card("H03", -20, 0, 0, 0, researchSet.Develop, null,1),
                Card("H04", -50, 10, -10, 20, researchSet.Develop,  null,1),
                Card("H05", -60, 0, 0, 20, researchSet.Develop, null,1),
                Card("H06", -60, 10, 0, 20, researchSet.Develop, null,1),
                Card("H07", -20, 0, -10, 10, researchSet.Develop, null,1),
                Card("H08", -30, 0, 0, 0, researchSet.Develop, null,2),
                Card("H09", -30, 0, -10, 10, researchSet.Develop, null,2),
                //mosse.add(Mossa("H10", 0, 15, 10, 20, Mossa.researchSet.Develop, 1));
                //mosse.add(Mossa("H11", 0, 15, 0, -20, Mossa.researchSet.Develop, 1));
                Card("H12", -40, 0, -10, 20, researchSet.Develop, null,2),
                Card("H13", -40, 10, 0, 20, researchSet.Develop, null,2)
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

    fun setPlayerTimer(timeToFinish : Long, tickInterval : Long, onTick: () -> Unit, onFinish : () -> Unit) : CountDownTimer{
        val timer: CountDownTimer = object :  CountDownTimer(timeToFinish, tickInterval) {
            override fun onTick(millisUntilFinished: Long) {
                    onTick()
            }

            override fun onFinish() {
                    onFinish()
            }
        }
        timer.start()
        return timer
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

       var oldIndex = if(lastPlayer.equals("") || lastPlayer.equals(playersPerTeam.get(team)!![playersPerTeam.get(team)!!.size - 1])) -1
       else playersPerTeam.get(team)!!.indexOf(lastPlayer)

        if (oldIndex == playersPerTeam.get(team)!!.size) oldIndex = -1

        return playersPerTeam.get(team)!![oldIndex + 1]
    }

    fun nextLevel() : Int{
        masterLevelCounter++
        return masterLevelCounter
    }

    fun findCard(cardCode : String) : Card?{
       return cardsMap.get(cardCode)
    }

    //funzioni per calcolare i dati di squadra in un livello

    fun evaluatePoints (level : Int, map : Map<String, String>, moves : Int) : TeamInfo{

        var points : Int = 0

        val exactCardPoints :  Int = 100
        val nearlyExactCardPoints :  Int = 75
        val wrongCardPoints : Int = 50
        val targetReachedPoints :  Int = 200
        val movesNegPoints : Int = 5

        val budget =
            zoneMap.get(level)?.budget?.plus(
                map.values.map { a -> cardsMap.get(a)!!.money }.toList().sum()
            )
        val energy = zoneMap.get(level)?.initEnergy?.plus(
            map.values.map { a -> cardsMap.get(a)!!.energy }.toList().sum()
        )
        val smog = zoneMap.get(level)?.initSmog?.plus(
            map.values.map { a -> cardsMap.get(a)!!.smog }.toList().sum()
        )
        val comfort = zoneMap.get(level)?.initComfort?.plus(
            map.values.map { a -> cardsMap.get(a)!!.comfort }.toList().sum()
        )

        // se il non c'è un valore sensato per il level metto a null anche points (le moves le metto a null quando chiamo newStats in gameModel)
        if (zoneMap.get(level)!=null){
                when {
                    energy!! > zoneMap.get(level)!!.TargetE -> points += targetReachedPoints
                    smog!! < zoneMap.get(level)!!.TargetA -> points += targetReachedPoints
                    comfort!! > zoneMap.get(level)!!.TargetC -> points += targetReachedPoints
                }

            map.filter { a -> zoneMap.get(level)!!.optList.contains(a.value) }.forEach{
                if (zoneMap.get(level)!!.optList.indexOf(it.value) == months.indexOf(it.key)) points += exactCardPoints
                else points += nearlyExactCardPoints
            }

            map.filter { a -> !zoneMap.get(level)!!.optList.contains(a.value) }.forEach {
                points += wrongCardPoints //todo: trovare un modo più intelligente per dire valutare i punti di un valore fisso per le carte sbagliate
            }

            points -= (moves * movesNegPoints)


        }

        return TeamInfo(budget, smog, energy, comfort, if (points==0) null else points, moves)


    }


}
