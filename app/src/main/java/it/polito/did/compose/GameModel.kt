package it.polito.did.compose

import android.annotation.SuppressLint
import android.media.Image
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.animation.core.SnapSpec
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.polito.did.compose.Components.pushResult
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.DataClasses.TeamInfo
import it.polito.did.compose.DataClasses.dialogData
import kotlinx.coroutines.delay
import java.util.stream.Collectors

class GameModel : ViewModel() {

    //todo: aggiungi una variabile di errore che se true mi porta ad uno screen apposta ovunque io sia

    val gameLogic = GameLogic(viewModelScope)
    private val db = Firebase.database.reference

    private var count: Int = 0 //todo: variabile per dare un nome in test ai players, da sostituire con i vari uid

    var playerCounter: MutableLiveData<Int> = MutableLiveData()


    val startMatch : MutableLiveData<Boolean> = MutableLiveData(false)
    val ongoingLevel : MutableLiveData<Boolean> = MutableLiveData(false)
    var levelTimerCountdown : MutableLiveData<Int?> = MutableLiveData(null)
    var masterLevelStatus : MutableLiveData<String> = MutableLiveData("preparing")

    var playedCardsPerTeam : MutableLiveData<Map<String, Map<String, String>?>> = MutableLiveData(
        mutableMapOf("team1" to null, "team2" to null, "team3" to null, "team4" to null)
    )

    var teamsStats : MutableLiveData<Map<String, TeamInfo?>> = MutableLiveData(
        mutableMapOf("team1" to null, "team2" to null, "team3" to null, "team4" to null)
    )

    var ableToPlayPerTeam : MutableLiveData<MutableMap<String, String>> = MutableLiveData(
        mutableMapOf("team1" to "", "team2" to "", "team3" to "", "team4" to "" )
    )

    // variabili lato player
    var playerCards : MutableLiveData<MutableList<Card>> = MutableLiveData<MutableList<Card>>()
    var team : String = "null"
    var playerLevelCounter: MutableLiveData<Long> = MutableLiveData(0)
    var playerLevelStatus : MutableLiveData<String?> = MutableLiveData(null)
    var playerTimerCountdown : MutableLiveData<Int?> = MutableLiveData(null)
    var playerTimer : MutableLiveData<CountDownTimer?> = MutableLiveData(null)
    var pushResult : MutableLiveData<Pair<pushResult, String?>> = MutableLiveData(it.polito.did.compose.Components.pushResult.CardDown to null)
    var showDialog : MutableLiveData<dialogData?> = MutableLiveData(null)

    //variabili sia master che player per schermate di splash e error
    var splash : MutableLiveData<Boolean> = MutableLiveData(false)
    var error : MutableLiveData<Boolean> = MutableLiveData(false)

    //todo: ovunque ci sia il test nei child di firebase devi inserire l'uid del master

    // logica lato master

        //funzioni per creare un match e far connettere i players
        fun createNewMatch() {
            //db.child("matches").setValue(gameLogic.firebaseAuth.uid)
            db.child("matches").setValue(mapOf("test" to mapOf("level" to "", "players" to "", "teams" to "")))
        }

        fun setPlayerCounter(){
            db.child("matches").child("test").child("players").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    playerCounter.value = snapshot.children.count()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

            //funzioni per preparare la partita, si chiamano a cascata in quest'ordine
            fun prepareMatch(){

                val dbPoint =  db.child("matches").child("test").child("players")

                dbPoint.get().addOnSuccessListener {

                    dbPoint.setValue(gameLogic.selectTeamForPlayers(it)).addOnSuccessListener {
                        db.child("matches").child("test").child("teams").setValue(gameLogic.createTeamsOnDb())
                            .addOnCompleteListener {
                                val newLevel : Int = gameLogic.nextLevel()
                                giveCardsToPlayers(newLevel)
                                // giveCardsToPlayer chiama all' onComplete setStartingCardsPerLevel, e lei chiama addPlayedCardsListener
                            }
                    }.addOnFailureListener {
                        // situazione in cui ho preso il riferimento players del db ma non sono riuscito a dare il team a tutti i players
                        throw it
                    }
                }.addOnFailureListener {
                    // situazione in cui non riesco ad accedere a players
                    throw it
                }
            } // funzione per inserire tutti i dati ed i listener necessari nel db

                fun giveCardsToPlayers (level: Int) {

                             val cardsPerPlayerMap : Map<String, Map<String, Boolean>> = gameLogic.cardsToPlayers(level)

                             db.child("matches").child("test").child("players").get()
                                 .addOnSuccessListener {
                                     var playersServed : Int = 0
                                     for (player in it.children) {
                                         db.child("matches").child("test").child("players").child(player.key!!)
                                             .child("ownedCards").setValue(cardsPerPlayerMap.get(player.key))
                                             .addOnCompleteListener { playersServed++
                                             if(playersServed==playerCounter.value) setStartingCardsPerLevel(level)
                                             }
                                     }
                                 }.addOnFailureListener {
                                     // situazione in cui riesco a cambiare il livello ma non riesco a prendere players dal db
                                     throw it
                                 }

                }

                    fun setStartingCardsPerLevel(level : Int){

                        val startingCardMap : Map<String, String> = gameLogic.zoneMap.get(level)!!.startingList.stream()
                            .collect(Collectors.toMap({a ->
                                if (a.equals("no card"))
                                    "void"
                                else
                                    gameLogic.months[gameLogic.zoneMap.get(level)!!.startingList.indexOf(a)]
                                                      }, {b -> b}))

                        var startingCardsServed : Int = 0
                        for (team in gameLogic.playersPerTeam.keys){

                            db.child("matches").child("test").child("teams").child(team)
                                .child("playedCards").setValue(startingCardMap).addOnFailureListener {
                                    // failure
                                }.addOnCompleteListener { startingCardsServed ++
                                    //todo: fai in modo che ci sia un modo per contare i team se sono meno di 4 giocatori
                                    if (startingCardsServed==4) {
                                        addPlayedCardsListener()
                                        startMatch.value = true
                                    }
                                }
                        }
                    } //chiamata in start match

                        fun addPlayedCardsListener(){
                            var avatarMap : MutableMap<String, Map<String, String>?> = mutableMapOf()
                           for (team in listOf("team1", "team2", "team3", "team4")){
                               db.child("matches").child("test").child("teams").child(team).child("playedCards")
                                   .addValueEventListener(object : ValueEventListener{
                                       override fun onDataChange(snapshot: DataSnapshot) {
                                           val map: MutableMap<String, String> = mutableMapOf()
                                           for (playedCard in snapshot.children){
                                               if (!playedCard.value.toString().equals("no card"))
                                                map.put(playedCard.key.toString(), playedCard.value.toString())
                                           }
                                           newStatsPerTeam(team, map)
                                           avatarMap = playedCardsPerTeam.value!!.toMutableMap()
                                           avatarMap.remove(team)
                                           avatarMap.put(team, map)
                                           playedCardsPerTeam.value = avatarMap
                                       }
                                       override fun onCancelled(error: DatabaseError) {
                                           TODO("Not yet implemented")
                                       }

                                   })
                           }
                        } // questa la devono chiamare sia master che player

                            fun addTeamTimeOutListener(){

                                for (team in listOf("team1", "team2", "team3", "team4")){
                                    db.child("matches").child("test").child("teams").child(team).child("ableToPlay")
                                        .addValueEventListener(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                               if(snapshot.value.toString().equals("")){
                                                   val newPLayer: String = gameLogic.findNextPlayer(team, ableToPlayPerTeam.value!!.get(team)!!)
                                                   ableToPlayPerTeam.value!!.replace(team, newPLayer)
                                                   setPlayerAbleToPLay(newPLayer, team)
                                               }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                }
                            } // da aggiungere per iniziare a giocare (chiamata in startLevel)

                                fun setPlayerAbleToPLay(newPlayer : String, team: String){
                                    db.child("matches").child("test").child("teams").child(team)
                                        .child("ableToPlay").setValue(newPlayer).addOnSuccessListener {

                                        }.addOnFailureListener {  }
                                } //chiamata in addTeamTimeOutListener

    fun prepareLevel(level : Int){
        if (gameLogic.masterLevelCounter == 1){
            val levelValue = mapOf<String, Any> ("status" to masterLevelStatus.value!!, "count" to level)
            db.child("matches").child("test").child("level").setValue(levelValue).addOnSuccessListener {
                masterLevelStatus.value = "play"
            }
        }
        else {
            //todo: controlla che se parte il secondo livello non esploda tutto (si dovrebbe anche bloccare il timer del player alla fine del timer di livello)
                masterLevelStatus.value = "play"
                giveCardsToPlayers(level)
                setStartingCardsPerLevel(level)
        }
    }


    fun startLevel(){

        val onTick : () -> Unit ={
            levelTimerCountdown.value = levelTimerCountdown.value!! - 1
        }

        val onFinish : () -> Unit ={
              ongoingLevel.value = false
              masterLevelStatus.value = "preparing"
              levelTimerCountdown.value = null
              gameLogic.masterLevelCounter = gameLogic.nextLevel()
              val levelValue = mapOf<String, Any> ("status" to masterLevelStatus.value!!, "count" to gameLogic.masterLevelCounter)
              db.child("matches").child("test").child("level").setValue(levelValue)
        }

        db.child("matches").child("test").child("level").child("status").setValue("play").addOnSuccessListener {
            addTeamTimeOutListener()
            ongoingLevel.value = true
            levelTimerCountdown.value = 420
            gameLogic.setLevelTimer(onTick, onFinish)
        }
    }

    fun newStatsPerTeam(team :String, map: MutableMap<String, String>){
        val avatarMap : MutableMap<String, TeamInfo?> = teamsStats.value!!.toMutableMap()

            val moves : Int = if (avatarMap.get(team)!= null && avatarMap.get(team)!!.nullCheck()) avatarMap.get(team)!!.moves!! else 0
            val lv : Int = if (playerLevelCounter.value!!.toInt()==0) gameLogic.masterLevelCounter else playerLevelCounter.value!!.toInt()

            avatarMap.remove(team)
            //moves + 1 serve per fare in modo che le nuove team info tengano conto di una mossa in più
            // (questa funzone viene chiamata ad ogni mossa)
            avatarMap.put(team, gameLogic.evaluatePoints(lv, map, moves + 1))
            teamsStats.value = avatarMap

    } //chiamata in addPlayedCardsListener


    //logica lato player

    //todo : ovunque ci sia la chiave dell'utente (ad es. 1) devi inserire il suo uid


    fun joinMatch(){
        count++
        db.child("matches").child("test").child("players")
            .child(count.toString()).setValue("")  //.child(gameLogic.firebaseAuth.uid.toString()).setValue(playerCounter))

    }

    //todo: listen to level change fa partire la partita anche se il player non si è mai connesso
    fun listenToLevelChange(){
        db.child("matches").child("test").child("level")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.value!!.equals("")){
                        //questo if controlla che il value del level sia cambiato effettivamente (e non che l'ondata change sia stato chiamato e basta)
                        //e che lo stato sia preparing
                        if ( playerLevelCounter.value != snapshot.child("count").value as Long &&
                            playerLevelStatus.value != snapshot.child("status").value as String &&
                            snapshot.child("status").value.toString().equals("preparing"))
                            splash.value = true


                        playerLevelCounter.value = snapshot.child("count").value as Long
                        playerLevelStatus.value = snapshot.child("status").value as String

                        // questo dovrebbe bloccare il timer quando il livello finisce e fare le operazioni di fine turno
                        if (snapshot.child("status").value.toString().equals("preparing") && playerTimerCountdown.value != null){

                                playerTimer.value?.cancel()
                                playerTimer.value = null
                                playerTimerCountdown.value = null
                                setTimeOutTrue()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    } //chiamata in camera screen mentre il player attende la partita

    //per preparare il giocatore alla partita
        fun playerReadyToPlay(){
            bindCardsForPlayer()
            notifyAbleToPlayChange()
            addPlayedCardsListener()
        }

        fun bindCardsForPlayer(){
            db.child("matches").child("test").child("players").child("1").child("ownedCards")
                .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list : MutableList<Card> = mutableListOf()
                    for (card in snapshot.children){
                        val crd = gameLogic.findCard(card.key!!)
                        if(crd!=null)
                        {
                            list.add(crd)
                        }
                    }
                    playerCards.value = list
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        fun notifyAbleToPlayChange(){

            // da chiamare quando si passa alla schermata di gioco
            // (per arrivare alla schermata di gioco devi popolare il resto del db con il master,
            // quindi popola i failure)
            //todo: togli 1 e metti il player uid
             db.child("matches").child("test").child("players")
                .child("1").child("team").get().addOnCompleteListener {
                    team = it.getResult()!!.value.toString()
                     db.child("matches").child("test").child("teams").child(team).child("ableToPlay")
                         .addValueEventListener(object : ValueEventListener{
                             override fun onDataChange(snapshot: DataSnapshot) {
                                 if (snapshot.value.toString().equals("1") && playerLevelCounter.value != 0L && playerLevelStatus.value?.equals("play") == true){
                                     playerTimerCountdown.value = 62
                                     val onTick : () -> Unit = {
                                         if (playerTimerCountdown!=null){
                                         //se non è ancora finito il tempo di livello eseguo
                                         playerTimerCountdown.value = playerTimerCountdown.value as Int - 1
                                         }
                                     }
                                     val onFinish : () -> Unit = {
                                         //se il timer finisce mentre c'è ancora tempo nel livello eseguo qui le operazioni di fine turno
                                         // (altrimenti in listenToLevelChange)
                                         if (playerTimer.value!=null){
                                             playerTimer.value!!.cancel()
                                             playerTimerCountdown.value = null
                                             setTimeOutTrue()
                                         }
                                     }
                                     //se c'è anche la schermata di splash attiva do un secondo in più al timer perchè il primo giocatore parta comunque da 60
                                     playerTimer.value = gameLogic.setPlayerTimer( if(splash.value!!) 63000 else 62000, 1000, onTick, onFinish)
                                 }
                             }

                             override fun onCancelled(error: DatabaseError) {
                                 TODO("Not yet implemented")
                             }
                         })



                }.addOnFailureListener {
                    // failure
                 }
        } //da chiamare per iniziare a giocare



    fun setTimeOutTrue(){
        Log.d("team in set time out true : ", team)
        db.child("matches").child("test").child("teams").child(team)
            .child("ableToPlay").setValue("").addOnSuccessListener {

            }.addOnFailureListener {
                // failure
            }
    } // quando il timer va a 0 chima questa per lasciare il turno libero per il prossimo giocatore

    fun playCardInPos ( pos: Int, cardCode: String){

              db.child("matches").child("test").child("teams").child(team)
                  .child("playedCards").child(gameLogic.months[pos]).setValue(cardCode)
                  .addOnSuccessListener {
                      db.child("matches").child("test").child("players").child("1")
                          .child("ownedCards").child(cardCode).removeValue()
                  }.addOnFailureListener {

                  }

    } // questa la si chiama direttamente nello screen di gioco,

    fun retriveCardFromPos(pos : Int){
        val month : String = gameLogic.months[pos]
        val cardCode : String = playedCardsPerTeam.value!!.get(team)!!.get(month)!!

        db.child("matches").child("test").child("teams").child(team)
            .child("playedCards").child(month).removeValue().addOnSuccessListener {
                db.child("matches").child("test").child("players").child("1")
                    .child("ownedCards").child(cardCode).setValue(true)
            }.addOnFailureListener {

            }
    }

    fun getBudgetSnapshot (playedCards : List<String>?) : Int{
        if(playedCards!=null && gameLogic.zoneMap.get(playerLevelCounter.value!!.toInt())?.budget!=null)
        { return (gameLogic.zoneMap.get(playerLevelCounter.value!!.toInt())!!.budget - playedCards.map { a -> gameLogic.cardsMap.get(a)!!.money }.sum())
        }
        else return 0

    }

}