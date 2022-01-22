package com.nullvoid.crimson.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.customs.DbHelper
import com.nullvoid.crimson.databinding.LayoutAuthPhoneBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class AuthFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutAuthPhoneBinding
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String? = null
    private var listener: AuthInterface? = null

    companion object {
        interface AuthInterface {
            fun onVerify()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAuthPhoneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendOtp.setOnClickListener {
            var phone = binding.mobileField.text.toString()
            if (Pattern.matches("^[6-9][0-9]{9}\$", phone)) {
                binding.mobileLayout.error = null
                binding.mobileLayout.isErrorEnabled = false
                phone = "+91$phone"
                authWithPhone(phone)
            } else {
                binding.mobileLayout.error = "Enter valid mobile number"
                binding.mobileLayout.isErrorEnabled = true
            }
        }
        binding.verify.setOnClickListener {
            val code = binding.otpField.text.toString().trim()
            val cred = verificationId?.let { it1 -> PhoneAuthProvider.getCredential(it1, code) }
            if (cred != null) {
                firebaseWithCred(cred)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AuthInterface
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun firebaseWithCred(credential: PhoneAuthCredential) {
        binding.progress.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Firebase.auth.signInWithCredential(credential).await()
                DbHelper(requireContext()).loginSetup()
                listener?.onVerify()
                launch(Dispatchers.Main){
                    dismiss()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                    binding.sendOtp.isEnabled = true
                    binding.progress.visibility = View.GONE
                    binding.verify.visibility = View.GONE
                    binding.otpLayout.visibility = View.GONE
                }
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(cred: PhoneAuthCredential) {
            firebaseWithCred(cred)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
            binding.sendOtp.isEnabled = true
            binding.verify.visibility = View.GONE
            binding.otpLayout.visibility = View.GONE
            binding.progress.visibility = View.GONE
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            verificationId = id
            resendToken = token
            binding.verify.visibility = View.VISIBLE
            binding.otpLayout.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
        }
    }

    private fun authWithPhone(phoneNumber: String) {
        if (activity != null) {
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(callbacks)
                .build()
            binding.sendOtp.isEnabled = false
            binding.progress.visibility = View.VISIBLE
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}