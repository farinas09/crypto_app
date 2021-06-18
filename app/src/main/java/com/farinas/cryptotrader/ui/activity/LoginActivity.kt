package com.farinas.cryptotrader.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.farinas.cryptotrader.R
import com.farinas.cryptotrader.databinding.ActivityLoginBinding
import com.farinas.cryptotrader.model.User
import com.farinas.cryptotrader.network.Callback
import com.farinas.cryptotrader.network.FirestoreService
import com.farinas.cryptotrader.network.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestoreService: FirestoreService
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())
        setContentView(binding.root)
    }


    fun onStartClicked(view: View) {
        view.isEnabled = false
        val username: String = binding.username.text.toString()
        if(username.trim().isNotEmpty()) {
            auth.signInAnonymously()
                .addOnCompleteListener{task ->
                    if(task.isSuccessful) {
                        firestoreService.findUserById(username, object : Callback<User?>{
                            override fun onSuccess(result: User?) {
                                if(result == null) {
                                    val user = User()
                                    user.username = username
                                    saveUserAndStartMainActivity(user, view)
                                } else {
                                    startMainActivity(username)
                                }
                            }

                            override fun onFailded(exception: Exception) {
                                showErrorMessage(view)
                            }

                        })
                    } else {
                        showErrorMessage(view)
                        view.isEnabled = true
                    }
                }

        }
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreService.setDocument(user, USERS_COLLECTION, user.username, object : Callback<Void>{
            override fun onSuccess(result: Void?) {
                startMainActivity(user.username)
            }

            override fun onFailded(exception: Exception) {
                showErrorMessage(view)
                view.isEnabled = true
            }

        })

    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}