package com.farinas.cryptotrader.adapter

import com.farinas.cryptotrader.model.Crypto

/**
 * Created by efarinas on $ 6/17/21.
 */
interface CryptosAdapterListener {
    fun onBuyCryptoClicked(crypto: Crypto)
}