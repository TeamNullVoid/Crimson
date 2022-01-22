package com.nullvoid.crimson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nullvoid.crimson.adapters.RequestAdapter
import com.nullvoid.crimson.customs.DbHelper
import com.nullvoid.crimson.databinding.ActivityRequestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private lateinit var adapter: RequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        adapter = RequestAdapter(this)
        binding.requestList.hasFixedSize()
        binding.requestList.adapter = adapter
        binding.requestList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.requestToolbar.setNavigationOnClickListener { finish() }
        loadRequests()
    }

    private fun loadRequests() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val users = DbHelper(this@RequestActivity).loadRequests()
                runOnUiThread {
                    if (users.isEmpty()) {
                        showNullView()
                    } else {
                        hideNullView()
                        adapter.setData(users)
                    }
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_LONG).show()
            } finally {
                runOnUiThread {
                    binding.requestProgress.visibility = View.GONE
                }
            }
        }
    }

    fun hideNullView() {
        binding.requestEmptyIcon.visibility = View.GONE
        binding.requestEmptyText.visibility = View.GONE
        binding.requestList.visibility = View.VISIBLE
    }

    fun showNullView() {
        binding.requestList.visibility = View.GONE
        binding.requestEmptyText.visibility = View.VISIBLE
        binding.requestEmptyIcon.visibility = View.VISIBLE
    }

}