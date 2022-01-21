package com.nullvoid.crimson.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nullvoid.crimson.databinding.LayoutAuthPhoneBinding

class AuthPage : BottomSheetDialogFragment() {
    private lateinit var binding: LayoutAuthPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAuthPhoneBinding.inflate(layoutInflater)
        return binding.root
    }
}