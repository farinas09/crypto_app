package com.farinas.cryptotrader.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.farinas.cryptotrader.network.RealtimeDataListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    private lateinit var binding: ActivityTraderBinding
    private lateinit var firestoreService: FirestoreService
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)
    private var user: User? = null
    private var username: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        username = intent.extras?.get(USERNAME_KEY) as String? ?: ""

        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
        binding.usernameTextView.text = username

        setupRecyclerView()
        loadCryptos()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                    .setAction("Info", null).show()
        }
    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object : Callback<List<Crypto>?> {
            override fun onSuccess(cryptoList: List<Crypto>?) {

                firestoreService.findUserById(username, object : Callback<User?> {
                    override fun onSuccess(result: User?) {
                        user = result
                        if (user!!.cryptosList == null) {
                            val userCryptoList = mutableListOf<Crypto>()

                            for (crypto in cryptoList!!) {
                                val cryptoUser = Crypto()
                                cryptoUser.name = crypto.name
                                cryptoUser.available = 0
                                cryptoUser.imageUrl = crypto.imageUrl
                                userCryptoList.add(cryptoUser)
                            }
                            user!!.cryptosList = userCryptoList
                            firestoreService.updateUser(user!!, null)
                        }
                        loadUserCryptos()
                        addRealtimeListeners(user!!, cryptoList!!)

                    }

                    override fun onFailded(exception: Exception) {
                        showGeneralServerErrorMessage()
                    }
                })

                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptoList = cryptoList!!
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailded(exception: Exception) {
                Log.e("TraderActivity", "error loading criptos", exception)
                showGeneralServerErrorMessage()
            }
        })
    }

    private fun addRealtimeListeners(user: User, cryptosList: List<Crypto>) {

        firestoreService.listenForUpdates(user, object : RealtimeDataListener<User> {
            override fun ondataChange(updatedData: User) {
                this@TraderActivity.user = updatedData
                loadUserCryptos()
            }

            override fun onError(exeption: Exception) {
                showGeneralServerErrorMessage()
            }
        })

        firestoreService.listenForUpdates(cryptosList, object : RealtimeDataListener<Crypto> {
            override fun ondataChange(updatedData: Crypto) {
                for ((pos, crypto) in cryptosAdapter.cryptoList.withIndex()) {
                    if (crypto.name.equals(updatedData.name)) {
                        crypto.available = updatedData.available
                        cryptosAdapter.notifyItemChanged(pos)
                    }
                }
            }

            override fun onError(exeption: Exception) {
                showGeneralServerErrorMessage()
            }
        })

    }

    private fun loadUserCryptos() {
        runOnUiThread {
            if (user != null && user!!.cryptosList != null) {
                binding.infoPanel.removeAllViews()
                for (crypto in user!!.cryptosList!!) {
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
        if (crypto.available > 0) {
            for (userCrypto in user!!.cryptosList!!) {
                if (userCrypto.name == crypto.name) {
                    userCrypto.available += 1
                    break
                }
            }
            crypto.available--
            println("contentcripto " + crypto.available)
            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        }
    }
}