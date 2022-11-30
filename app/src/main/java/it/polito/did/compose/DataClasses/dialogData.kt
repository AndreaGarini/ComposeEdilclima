package it.polito.did.compose.DataClasses

import android.media.Image
import android.provider.ContactsContract.CommonDataKinds.Im
import it.polito.did.compose.GameModel

class dialogData(val text : String, imageId : String?, val timed : Boolean, val title : String?, val buttonText : String) {

    val image : Image? = findImage(imageId)

    fun findImage(imageId : String?) : Image?{
        if (imageId!=null){
            //todo: aggiungi il metodo per cercare l'immagine dall'id
            return null
        }
        else return null

    }
}

object imageIdList{}