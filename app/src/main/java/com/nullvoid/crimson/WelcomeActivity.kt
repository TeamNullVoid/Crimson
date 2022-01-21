package com.nullvoid.crimson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nullvoid.crimson.auth.AuthPage
import com.nullvoid.crimson.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueButton.setOnClickListener {
            supportFragmentManager.beginTransaction().add(AuthPage(), AuthPage::class.java.simpleName).commit()
        }
    }
}