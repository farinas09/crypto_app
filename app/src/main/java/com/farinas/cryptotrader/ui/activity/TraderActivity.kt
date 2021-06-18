package com.farinas.cryptotrader.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.farinas.cryptotrader.R
import com.farinas.cryptotrader.adapter.CryptosAdapter
import com.farinas.cryptotrader.adapter.CryptosAdapterListener
import com.farinas.cryptotrader.databinding.ActivityTraderBinding
import com.farinas.cryptotrader.databinding.CoinInfoBinding
import com.farinas.cryptotrader.databinding.CryptoRowBinding
import com.farinas.cryptotrader.model.Crypto
import com.farinas.cryptotrader.model.User
import com.farinas.cryptotrader.network.Callback
import com.farinas.cryptotrader.network.FirestoreService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    private lateinit var binding: ActivityTraderBinding
    private lateinit var firestoreService: FirestoreService
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        binding.usernameTextView.text = intent.extras?.get(USERNAME_KEY) as String? ?: ""

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
                if (result != null) {
                    getUserData(result)
                    this@TraderActivity.runOnUiThread{

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

    private fun getUserData(cryptos: List<Crypto>) {
        auth.currentUser?.let {
            firestoreService.findUserById(it.uid, object: Callback<User?> {
                override fun onSuccess(result: User?) {
                    user = result
                    if (user!!.cryptosList == null) {
                        val userCryptoList = mutableListOf<Crypto>()
                        for (crypto in cryptos) {
                            val cryptoUser = Crypto(
                                crypto.name,
                                crypto.imageUrl,
                                crypto.available,
                                crypto.id
                            )
                            userCryptoList.add(cryptoUser)
                        }
                        user!!.cryptosList = userCryptoList
                        firestoreService.updateUser(user!!, null)
                    }
                    loadUserCryptos()
                }

                override fun onFailded(exception: Exception) {
                    showGeneralServerErrorMessage()
                }

            })
        }
    }

    private fun loadUserCryptos() {
        runOnUiThread{
            if (user!=null && user!!.cryptosList!=null) {
                binding.infoPanel.removeAllViews()
                for (crypto in user!!.cryptosList!!){
                    addUserCryptoRow(crypto)
                }
            }
        }
    }

    private fun addUserCryptoRow(crypto: Crypto) {
        val infoBinding = CoinInfoBinding.bind(LayoutInflater.from(this).inflate(R.layout.coin_info, binding.infoPanel, false))
        infoBinding.coinLabel.text = getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(infoBinding.coinIcon)
        binding.infoPanel.addView(infoBinding.root)
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