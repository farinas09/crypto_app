package com.farinas.cryptotrader.network

/**
 * Created by efarinas on $ 6/17/21.
 */
interface Callback <T> {
    fun onSuccess (result: T?)
    fun onFailded (exception: Exception)
}