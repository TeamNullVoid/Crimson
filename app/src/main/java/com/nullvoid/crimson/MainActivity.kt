package com.nullvoid.crimson

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nullvoid.crimson.adapters.MainPagerAdapter
import com.nullvoid.crimson.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.mainPager.adapter = MainPagerAdapter(this, supportFragmentManager)
        binding.mainTabLayout.setupWithViewPager(binding.mainPager)
        setupTabs()
        binding.mainBottomBar.setOnMenuItemClickListener(this)
        binding.mainEmergencyButton.setOnClickListener {
//            startActivity(Intent(this, EmergencyActivity::class.java))
        }
        binding.mainBottomBar.setNavigationOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .add(MenuFragment(), MenuFragment::class.simpleName).commit()
        }
    }

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    private fun setupTabs() {
        binding.mainTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_contact)
        binding.mainTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_map)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_addUser -> supportFragmentManager.beginTransaction()
//                .add(SendRequestFragment(), SendRequestFragment::class.java.simpleName).commit()
//        }
        return true
    }

}