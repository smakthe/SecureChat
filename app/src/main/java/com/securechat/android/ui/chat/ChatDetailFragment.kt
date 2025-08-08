package com.securechat.android.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.securechat.android.SecureChatApplication
import com.securechat.android.databinding.FragmentChatDetailBinding
import com.securechat.android.ui.ViewModelFactory
import com.securechat.android.ui.chat.adapter.MessageAdapter

class ChatDetailFragment : Fragment() {

    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ChatDetailFragmentArgs by navArgs()
    private val viewModel: ChatDetailViewModel by viewModels {
        val app = requireActivity().application as SecureChatApplication
        ViewModelFactory(app.appContainer.userRepository, app.appContainer.messageRepository, app.appContainer.chatRepository, app.appContainer.twoFactorAuthManager)
    }
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupMessageInput()
    }

    private fun setupViewModel() {
        viewModel.setChatId(args.chatId)
        viewModel.setParticipantHandle(args.participantHandle)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(viewModel)

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.rvMessages.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.participant.observe(viewLifecycleOwner) { participant ->
            // Update UI with participant info
            activity?.title = "@${participant?.handle ?: ""}"
        }

        viewModel.messageText.observe(viewLifecycleOwner) { text ->
            if (binding.etMessage.text.toString() != text) {
                binding.etMessage.setText(text)
            }
            binding.btnSend.isEnabled = text.isNotBlank()
        }

        viewModel.isTyping.observe(viewLifecycleOwner) { isTyping ->
            binding.tvTyping.visibility = if (isTyping) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }

        binding.btnAttach.setOnClickListener {
            showAttachmentOptions()
        }
    }

    private fun setupMessageInput() {
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onMessageTextChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.markMessagesAsRead()
    }

    private fun showAttachmentOptions() {
        val options = arrayOf("Camera", "Gallery", "File")
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Attach File")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> Snackbar.make(binding.root, "Camera feature coming soon", Snackbar.LENGTH_SHORT).show()
                    1 -> Snackbar.make(binding.root, "Gallery feature coming soon", Snackbar.LENGTH_SHORT).show()
                    2 -> Snackbar.make(binding.root, "File picker coming soon", Snackbar.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}