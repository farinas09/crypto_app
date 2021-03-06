package com.farinas.cryptotrader.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

/**
 * Created by efarinas on $ 6/17/21.
 */
class Crypto(var name: String = "", var imageUrl: String = "", var available: Int = 0) {
    fun getDocumentId(): String {
        return name.toLowerCase()}
}