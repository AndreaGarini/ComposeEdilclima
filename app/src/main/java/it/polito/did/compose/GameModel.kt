package it.polito.did.compose

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
import it.polito.did.compose.DataClasses.Card
import it.polito.did.compose.DataClasses.TeamInfo
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

    var playedCardsPerTeam : MutableLiveData<Map<String, Map<String, String>?>> = MutableLiveData(
        mutableMapOf("team1" to null, "team2" to null, "team3" to null, "team4" to null)
    )

    var teamsStats : MutableLiveData<Map<String, TeamInfo?>> = MutableLiveData(
        mutableMapOf("team1" to null, "team2" to null, "team3" to null, "team4" to null)
    )

    // variabili lato player
    var playerCards : MutableLiveData<MutableList<Card>> = MutableLiveData<MutableList<Card>>()
    var ableToPLay : MutableLiveData<Boolean> = MutableLiveData(false)
    var team : String = "null"
    var level: MutableLiveData<Long> = MutableLiveData(0)
    var playerTimerCountdown : MutableLiveData<Int?> = MutableLiveData(null)

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
                                // giveCardsToPlayer chiama all' onComplete setStartingCardsPerLevel, e lei chiama addPlayedCardsListener e addTeamtImeOutListener
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
                                        addTeamTimeOutListener()
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
                                // metti il timer al player, il timer scatta quando l'utente diventa able to play e 0 blocca la giocata in locale
                                // e scrive solo time out true, se time out è true il master scrive il nuovo player in able to play
                                db.child("matches").child("test").child("teams")
                                    .addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (team in snapshot.children){
                                                if (team.child("ableToPlay").value == "")
                                                {
                                                   val newPLayer: String = gameLogic.findNextPlayer(team.key!!, team.child("ableToPlay").value.toString())
                                                    setPlayerAbleToPLay(newPLayer, team.key.toString())
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                            } // da aggiungere per iniziare a giocare

                                fun setPlayerAbleToPLay(newPlayer : String, team: String){
                                    db.child("matches").child("test").child("teams").child(team)
                                        .child("ableToPlay").setValue(newPlayer).addOnSuccessListener {

                                        }.addOnFailureListener {  }
                                } //chiamata in addTeamTimeOutListener


    fun startLevel(level : Int){

        levelTimerCountdown.value = 420
        val onTick : () -> Unit ={
            levelTimerCountdown.value = levelTimerCountdown.value!! - 1
        }

        val onFinish : () -> Unit ={
              levelTimerCountdown.value = null
              //todo: cosa succede quando il livello finisce?
        }

        db.child("matches").child("test").child("level").setValue(level).addOnSuccessListener {
            ongoingLevel.value = true
            gameLogic.setLevelTimer(onTick, onFinish)
        }
    }

    fun newStatsPerTeam(team :String, map: MutableMap<String, String>){
        val avatarMap : MutableMap<String, TeamInfo?> = teamsStats.value!!.toMutableMap()
        val budget =
            gameLogic.zoneMap.get(level.value!!.toInt())?.budget?.plus(
                map.values.map { a -> gameLogic.cardsMap.get(a)!!.money }.toList().sum()
            )
        val energy = gameLogic.zoneMap.get(level.value!!.toInt())?.initEnergy?.plus(
            map.values.map { a -> gameLogic.cardsMap.get(a)!!.money }.toList().sum()
        )
        val smog = gameLogic.zoneMap.get(level.value!!.toInt())?.initSmog?.plus(
            map.values.map { a -> gameLogic.cardsMap.get(a)!!.money }.toList().sum()
        )
        val comfort = gameLogic.zoneMap.get(level.value!!.toInt())?.budget?.minus(
            map.values.map { a -> gameLogic.cardsMap.get(a)!!.money }.toList().sum()
        )
        avatarMap.remove(team)
        avatarMap.put(team, TeamInfo(budget, smog, energy, comfort))
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
                    if(!snapshot.value!!.equals("")) level.value = snapshot.value as Long
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
                .child("1").child("team").get().addOnSuccessListener {
                    team = it.value.toString()
                     db.child("matches").child("test").child("teams").child(team).child("ableToPlay")
                         .addValueEventListener(object : ValueEventListener{
                             override fun onDataChange(snapshot: DataSnapshot) {
                                 if (snapshot.value.toString().equals("1")){
                                     ableToPLay.value = true
                                     playerTimerCountdown.value = 60
                                     val onTick : () -> Unit = {
                                         playerTimerCountdown.value = playerTimerCountdown.value as Int - 1
                                     }
                                     val onFinish : () -> Unit = {
                                         ableToPLay.value = false
                                         playerTimerCountdown.value = null
                                         setTimeOutTrue()
                                     }
                                     gameLogic.setPlayerTimer(10000, 1000, onTick, onFinish)
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
        db.child("matches").child("test").child("teams").child(team)
            .child("ableToPlay").setValue("").addOnSuccessListener {
                Log.d("team timOut for team : ", team)
            }.addOnFailureListener {
                // failure
            }
    } // quando il timer va a 0 chima questa per lasciare il turno libero per il prossimo giocatore

    fun playCardInPos ( pos: Int, cardCode: String){

        //todo: fai giocare solo se il timer interno non è a 0, il che a sua volta vuol dire che il player è ableToPlay
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
        if(playedCards!=null && gameLogic.zoneMap.get(level.value!!.toInt())?.budget!=null)
        { return (gameLogic.zoneMap.get(level.value!!.toInt())!!.budget - playedCards.map { a -> gameLogic.cardsMap.get(a)!!.money }.sum())
        }
        else return 0

    }

}