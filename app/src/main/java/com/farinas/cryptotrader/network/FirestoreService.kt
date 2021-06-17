package com.farinas.cryptotrader.network

import com.farinas.cryptotrader.model.Crypto
import com.farinas.cryptotrader.model.User
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by efarinas on $ 6/17/21.
 */

const val CRYPTOS_COLLECTION = "cryptos"
const val USERS_COLLECTION = "users"

class FirestoreService (val firebaseFirestore: FirebaseFirestore) {

    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>){
        print(collectionName)
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener { exception -> callback.onFailded(exception) }
    }

    fun updateUser(user: User, callback: Callback<User>?) {
        firebaseFirestore.collection(USERS_COLLECTION).document(user.username)
            .update("cryptosList", user.cryptosList)
            .addOnSuccessListener { callback?.onSuccess(user) }
            .addOnFailureListener { exception -> callback?.let{it.onFailded(exception) } }
    }

    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTOS_COLLECTION).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }
}