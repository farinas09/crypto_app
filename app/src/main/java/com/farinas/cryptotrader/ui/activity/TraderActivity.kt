package com.farinas.cryptotrader.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.farinas.cryptotrader.R
import com.farinas.cryptotrader.databinding.ActivityTraderBinding
import com.google.android.material.snackbar.Snackbar

class TraderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTraderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                    .setAction("Info", null).show()
        }

    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(binding.fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
                .setAction("Info", null).show()
    }
}