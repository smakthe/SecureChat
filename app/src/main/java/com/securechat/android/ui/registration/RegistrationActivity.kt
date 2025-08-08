package com.securechat.android.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.securechat.android.databinding.ActivityRegistrationBinding
import com.securechat.android.ui.accountsetup.AccountSetupActivity // This should now be fine

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val TAG = "RegistrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity created and layout set.")

        binding.btnRegister.setOnClickListener {
            Log.d(TAG, "btnRegister onClick entered.")
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()

            Log.d(TAG, "Phone number extracted: '$phoneNumber'")

            if (phoneNumber.isEmpty()) {
                Log.d(TAG, "Phone number is empty. Showing toast.")
                Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Returning from onClick due to empty phone number.")
                return@setOnClickListener
            }

            Log.d(TAG, "Simulating registration for phone: $phoneNumber")
            Toast.makeText(this, "Simulating registration for $phoneNumber...", Toast.LENGTH_SHORT).show()

            Log.d(TAG, "Calling navigateToAccountSetup().")
            navigateToAccountSetup()
            Log.d(TAG, "Returned from navigateToAccountSetup() call in onClick.")
        }

        binding.btnConfirmOtp.setOnClickListener {
            Log.d(TAG, "btnConfirmOtp onClick entered.")
            val otpCode = binding.etOtpCode.text.toString().trim()
            if (otpCode.isEmpty()) {
                Log.d(TAG, "OTP code is empty. Showing toast.")
                Toast.makeText(this, "Please enter the OTP code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d(TAG, "Simulating OTP confirmation for OTP: $otpCode")
            Toast.makeText(this, "Simulating OTP confirmation...", Toast.LENGTH_SHORT).show()
            navigateToAccountSetup()
        }
        Log.d(TAG, "onCreate finished setting listeners.")
    }

    private fun navigateToAccountSetup() {
        Log.d(TAG, "navigateToAccountSetup() called.")
        val intent = Intent(this, AccountSetupActivity::class.java)
        startActivity(intent)
        Log.d(TAG, "startActivity(AccountSetupActivity) called.")
        finish()
        Log.d(TAG, "finish() called in navigateToAccountSetup().")
    }
}
