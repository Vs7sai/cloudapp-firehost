package com.v7techsolution.interviewfire

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.v7techsolution.interviewfire.databinding.DialogAuthBinding

class AuthDialog : DialogFragment() {
    
    private var _binding: DialogAuthBinding? = null
    private val binding get() = _binding!!
    
    private var authCallback: ((Boolean) -> Unit)? = null
    
    companion object {
        private const val TAG = "AuthDialog"
        
        fun newInstance(callback: (Boolean) -> Unit): AuthDialog {
            val dialog = AuthDialog()
            dialog.authCallback = callback
            return dialog
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAuthBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            signInWithEmail()
        }
        
        binding.btnSignUp.setOnClickListener {
            signUpWithEmail()
        }
        
        binding.btnCancel.setOnClickListener {
            authCallback?.invoke(false)
            dismiss()
        }
    }
    
    private fun signInWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        
        showProgress(true)
        
        AuthHelper.signInWithEmail(email, password) { success, errorMessage ->
            showProgress(false)
            
            if (success) {
                Toast.makeText(requireContext(), "Sign-in successful!", Toast.LENGTH_SHORT).show()
                authCallback?.invoke(true)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Sign-in failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun signUpWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        showProgress(true)
        
        AuthHelper.createAccountWithEmail(email, password) { success, errorMessage ->
            showProgress(false)
            
            if (success) {
                Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
                authCallback?.invoke(true)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Sign-up failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !show
        binding.btnSignUp.isEnabled = !show
        binding.btnCancel.isEnabled = !show
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
