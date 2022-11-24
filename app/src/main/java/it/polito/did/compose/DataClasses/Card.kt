package it.polito.did.compose.DataClasses

class Card (
    var code: String,
    var money: Int,
    var energy: Int,
    var smog: Int,
    var comfort: Int,
    var research: researchSet,
    var level : Int,
        ) {

    enum class researchSet {
        None, Needed, Develop
    }
}