package com.securechat.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.securechat.android.R
import com.securechat.android.SecureChatApplication
import com.securechat.android.databinding.FragmentSettingsBinding
import com.securechat.android.ui.ViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        val app = requireActivity().application as SecureChatApplication
        ViewModelFactory(app.appContainer.userRepository, app.appContainer.messageRepository, app.appContainer.chatRepository, app.appContainer.twoFactorAuthManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvCurrentHandle.text = "@${it.handle}"
                binding.ivCurrentAvatar.setImageResource(getAvatarResource(it.avatarId))
            }
        }

        viewModel.handleUpdateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SettingsViewModel.HandleUpdateResult.Success -> {
                    Snackbar.make(binding.root, "Handle updated successfully", Snackbar.LENGTH_SHORT).show()
                }
                is SettingsViewModel.HandleUpdateResult.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.is2FAEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switch2fa.isChecked = enabled
        }
    }

    private fun setupClickListeners() {
        binding.layoutChangeHandle.setOnClickListener {
            showChangeHandleDialog()
        }

        binding.ivCurrentAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.switch2fa.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggle2FA(isChecked)
        }

        binding.layoutBackup.setOnClickListener {
            Snackbar.make(binding.root, "Backup feature coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnPanicSettings.setOnClickListener {
            showPanicModeDialog()
        }

        binding.layoutTheme.setOnClickListener {
            Snackbar.make(binding.root, "Theme selection coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.layoutAbout.setOnClickListener {
            Snackbar.make(binding.root, "SecureChat v1.0\nEnd-to-end encrypted messaging", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showChangeHandleDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter new handle"
        val currentHandle = viewModel.currentUser.value?.handle ?: ""
        editText.setText(currentHandle)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Handle")
            .setMessage("Enter your new handle:")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newHandle = editText.text.toString().trim().removePrefix("@")
                viewModel.updateHandle(newHandle)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAvatarSelectionDialog() {
        val avatarIds = arrayOf(1, 2, 3, 4, 5)
        val avatarNames = avatarIds.map { "Avatar $it" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Avatar")
            .setItems(avatarNames) { _, which ->
                viewModel.updateAvatar(avatarIds[which])
            }
            .show()
    }

    private fun showPanicModeDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter panic password"
        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Activate Panic Mode")
            .setMessage("Enter a password to encrypt all your chats. You'll need this password to decrypt them later.")
            .setView(editText)
            .setPositiveButton("Activate") { _, _ ->
                val password = editText.text.toString()
                if (password.isNotEmpty()) {
                    viewModel.activatePanicMode(password)
                    Snackbar.make(binding.root, "Panic mode activated", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Password cannot be empty", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getAvatarResource(avatarId: Int): Int {
        return when (avatarId) {
            1 -> R.drawable.avatar_1
            2 -> R.drawable.avatar_2
            3 -> R.drawable.avatar_3
            4 -> R.drawable.avatar_4
            5 -> R.drawable.avatar_5
            else -> R.drawable.avatar_1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}