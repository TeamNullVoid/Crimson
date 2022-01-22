package com.nullvoid.crimson.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nullvoid.crimson.customs.SirenUtil
import com.nullvoid.crimson.customs.TorchUtil
import com.nullvoid.crimson.databinding.FragmentMenuBinding

class MenuFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var torchUtil: TorchUtil

    companion object {
        private var isTorchOn: Boolean = false
        private var isSirenOn: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.siren.isChecked = isSirenOn
        binding.torch.isChecked = isTorchOn
        torchUtil = TorchUtil(requireContext())

        if (!torchUtil.hasFlash()) binding.torch.isEnabled = false

        binding.siren.addOnCheckedChangeListener { _, isChecked ->
            isSirenOn = isChecked
            if (isSirenOn) {
                SirenUtil.start(requireContext())
            } else {
                SirenUtil.stop()
            }
        }

        binding.torch.addOnCheckedChangeListener { _, isChecked ->
            isTorchOn = isChecked
            torchUtil.toggleTorch(isTorchOn)
        }
    }

}