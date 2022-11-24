package it.polito.did.compose.DataClasses

class Zone(val id: Int, val TargetA: Int, val TargetE: Int, val TargetC: Int,
           var budget: Int,  var initSmog: Int, var initEnergy: Int, var initComfort: Int,
           optimalList: List<String>, startingList: List<String>) {

    var optList: List<String> = ArrayList()
    var startingList: List<String> = ArrayList()
    init{
        this.optList = optimalList
        this.startingList = startingList
    }

}