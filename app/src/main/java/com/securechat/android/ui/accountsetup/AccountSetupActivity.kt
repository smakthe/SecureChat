package com.securechat.android.ui.accountsetup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.securechat.android.databinding.ActivityAccountSetupBinding
import com.securechat.android.MainActivity // CORRECTED IMPORT

class AccountSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSetupBinding
    private val TAG = "AccountSetupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity created.")

        // Placeholder: Display assigned handle and avatar (logic to be implemented)
        binding.tvAssignedHandle.text = "AnonymousUser123" // Example
        // binding.ivAvatar.setImageResource(R.drawable.default_avatar) // Example

        binding.btnContinueToApp.setOnClickListener {
            Log.d(TAG, "Continue to app button clicked.")
            // Navigate to the main part of the app
            val intent = Intent(this, MainActivity::class.java) // Uses corrected MainActivity
            startActivity(intent)
            finishAffinity() // Finish this and previous registration/setup activities
        }

        binding.btnChangeAvatar.setOnClickListener {
            Log.d(TAG, "Change Avatar button clicked.")
            // TODO: Implement avatar change functionality
        }

        binding.btnChangeHandle.setOnClickListener {
            Log.d(TAG, "Change Handle button clicked.")
            // TODO: Implement handle change functionality
        }
    }
}
