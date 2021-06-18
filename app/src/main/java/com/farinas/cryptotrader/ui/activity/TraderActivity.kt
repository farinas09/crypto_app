package com.farinas.cryptotrader.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.farinas.cryptotrader.R
import com.farinas.cryptotrader.adapter.CryptosAdapter
import com.farinas.cryptotrader.adapter.CryptosAdapterListener
import com.farinas.cryptotrader.databinding.ActivityTraderBinding
import com.farinas.cryptotrader.model.Crypto
import com.farinas.cryptotrader.network.Callback
import com.farinas.cryptotrader.network.FirestoreService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    private lateinit var binding: ActivityTraderBinding
    private lateinit var firestoreService: FirestoreService
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        loadCryptos()
        setupRecyclerView()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                    .setAction("Info", null).show()
        }
    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object: Callback<List<Crypto>?>{
            override fun onSuccess(result: List<Crypto>?) {
                this@TraderActivity.runOnUiThread{
                    if (result != null) {
                        cryptosAdapter.cryptoList = result
                        cryptosAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailded(exception: Exception) {
                showGeneralServerErrorMessage()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = cryptosAdapter
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(binding.fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
                .setAction("Info", null).show()
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        TODO("Not yet implemented")
    }
}