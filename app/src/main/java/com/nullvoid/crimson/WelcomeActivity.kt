package com.nullvoid.crimson

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.auth.AuthFragment
import com.nullvoid.crimson.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity(), AuthFragment.Companion.AuthInterface {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var authFragment: AuthFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser == null) {
            authFragment = AuthFragment()
            binding.continueButton.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .add(authFragment, "AuthFragment").commit()
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onVerify() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}