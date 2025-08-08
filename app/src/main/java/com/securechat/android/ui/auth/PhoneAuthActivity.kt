package com.securechat.android.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.securechat.android.MainActivity
import com.securechat.android.SecureChatApplication
import com.securechat.android.databinding.ActivityPhoneAuthBinding
import com.securechat.android.ui.ViewModelFactory

class PhoneAuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPhoneAuthBinding
    private val viewModel: AuthViewModel by viewModels {
        val app = application as SecureChatApplication
        ViewModelFactory(app.appContainer.userRepository, app.appContainer.messageRepository, app.appContainer.chatRepository, app.appContainer.twoFactorAuthManager)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.authResult.observe(this) { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthViewModel.AuthResult.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnVerify.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                viewModel.registerUser(phoneNumber)
            } else {
                Snackbar.make(binding.root, "Please enter phone number", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}