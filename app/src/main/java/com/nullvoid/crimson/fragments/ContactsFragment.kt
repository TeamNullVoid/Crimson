package com.nullvoid.crimson.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nullvoid.crimson.adapters.LocalUserAdapter
import com.nullvoid.crimson.data.viewModel.CrimsonUserViewModel
import com.nullvoid.crimson.databinding.FragmentContactsBinding

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