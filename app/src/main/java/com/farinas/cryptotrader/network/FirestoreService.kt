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
        println(crypto.getDocumentId())
        firebaseFirestore.collection(CRYPTOS_COLLECTION).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }

    fun getCryptos(callback: Callback<List<Crypto>?>) {
        firebaseFirestore.collection(CRYPTOS_COLLECTION).get()
            .addOnSuccessListener { result ->
                for(document in result) {
                val cryptoList = result.toObjects(Crypto::class.java)
                callback.onSuccess(cryptoList)
            }
            }
            .addOnFailureListener { exception -> callback.onFailded(exception) }
    }

    fun findUserById(id: String, callback: Callback<User?>) {
        firebaseFirestore.collection(USERS_COLLECTION).document(id).get()
            .addOnSuccessListener { result ->
                if(result.data != null) {
                    callback.onSuccess(result.toObject(User::class.java))
                } else {
                    callback.onSuccess(null)
                }
                }
            .addOnFailureListener { exception -> callback.onFailded(exception) }
    }

    fun listenForUpdates(cryptos: List<Crypto>, listener: RealtimeDataListener<Crypto>){
        val cryptoRef = firebaseFirestore.collection(CRYPTOS_COLLECTION)
        for(crypto in cryptos) {
            cryptoRef.document(crypto.getDocumentId()).addSnapshotListener{ snapshot,e ->
                if(e != null) {
                    listener.onError(e)
                }
                if(snapshot != null && snapshot.exists()) {
                    listener.ondataChange(snapshot.toObject(Crypto::class.java)!!)
                }
            }
        }
    }

    fun listenForUpdates(user: User, listener: RealtimeDataListener<User>){
        val userRef = firebaseFirestore.collection(USERS_COLLECTION)
            userRef.document(user.username).addSnapshotListener{ snapshot,e ->
                if(e != null) {
                    listener.onError(e)
                }
                if(snapshot != null && snapshot.exists()) {
                    listener.ondataChange(snapshot.toObject(User::class.java)!!)
                }
            }

    }
}