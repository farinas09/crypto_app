package com.farinas.cryptotrader.network

import java.lang.Exception

/**
 * Created by efarinas on $ 6/17/21.
 */
interface RealtimeDataListener <T> {

    fun ondataChange(updatedData: T)

    fun onError(exeption: Exception)
}