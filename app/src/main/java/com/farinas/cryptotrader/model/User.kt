package com.farinas.cryptotrader.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

/**
 * Created by efarinas on $ 6/17/21.
 */
class User {
    var username: String = ""
    var cryptosList: List<Crypto>? = null
}