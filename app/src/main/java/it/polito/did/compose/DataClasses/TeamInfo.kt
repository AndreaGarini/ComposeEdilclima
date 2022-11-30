package it.polito.did.compose.DataClasses

class TeamInfo(val budget: Int?, val smog: Int?, val energy : Int?, val comfort : Int?, val points: Int?, val moves : Int?) {

    fun nullCheck () : Boolean{
        return smog!=null && energy != null && comfort != null && points != null && moves != null
    }
}