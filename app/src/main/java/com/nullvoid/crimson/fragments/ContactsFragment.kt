package com.nullvoid.crimson.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nullvoid.crimson.adapters.LocalUserAdapter
import com.nullvoid.crimson.customs.DbHelper
import com.nullvoid.crimson.data.viewModel.CrimsonUserViewModel
import com.nullvoid.crimson.databinding.FragmentContactsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private lateinit var adapter: LocalUserAdapter
    private lateinit var binding: FragmentContactsBinding
    private lateinit var viewModel: CrimsonUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentContactsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        adapter = LocalUserAdapter()
        binding.contactsListView.adapter = adapter
        binding.contactsListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.contactsListView.hasFixedSize()
        viewModel = ViewModelProvider(this)[CrimsonUserViewModel::class.java]
        viewModel.getUsers().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showNullView()
            } else {
                hideNullView()
            }
            adapter.setData(it)
        }
        binding.root.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    DbHelper(requireContext()).reloadFriendsData()
                } catch (e: Exception) {
                    launch(Dispatchers.IO) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Data Refreshed", Toast.LENGTH_LONG).show()
                    binding.root.isRefreshing = false
                }
            }
        }
    }

    private fun showNullView() {
        binding.contactsNullImage.visibility = View.VISIBLE
        binding.contactsNullText.visibility = View.VISIBLE
        binding.contactsListView.visibility = View.GONE
    }

    private fun hideNullView() {
        binding.contactsNullImage.visibility = View.GONE
        binding.contactsListView.visibility = View.VISIBLE
        binding.contactsNullText.visibility = View.GONE
    }


}