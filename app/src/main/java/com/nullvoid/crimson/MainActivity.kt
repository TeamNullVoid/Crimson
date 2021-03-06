package com.nullvoid.crimson

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.nullvoid.crimson.adapters.MainPagerAdapter
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.databinding.ActivityMainBinding
import com.nullvoid.crimson.fragments.MenuFragment

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuFragment: MenuFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPerms()
        init()
    }

    private fun checkPerms() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), Constant.RC_LOC_PERM
            )
        }
    }

    private fun init() {
        binding.mainPager.adapter = MainPagerAdapter(this, supportFragmentManager)
        binding.mainTabLayout.setupWithViewPager(binding.mainPager)
        setupTabs()
        binding.mainBottomBar.setOnMenuItemClickListener(this)
        menuFragment = MenuFragment()
        binding.mainEmergencyButton.setOnClickListener {
        }
        binding.mainBottomBar.setNavigationOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(MenuFragment(), MenuFragment::class.simpleName).commit()
        }
        binding.mainEmergencyButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EmergencyActivity::class.java
                )
            )
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
        if (item.itemId == R.id.add_user) {
            startActivity(Intent(this, AddCrimsonUser::class.java))
        } else if (item.itemId == R.id.requests) {
            startActivity(Intent(this, RequestActivity::class.java))
        }
        return true
    }

}