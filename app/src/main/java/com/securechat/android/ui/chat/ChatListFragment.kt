package com.securechat.android.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.securechat.android.SecureChatApplication
import com.securechat.android.databinding.FragmentChatListBinding
import com.securechat.android.ui.ViewModelFactory
import com.securechat.android.ui.chat.adapter.ChatListAdapter
import android.widget.EditText

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatListViewModel by viewModels {
        val app = requireActivity().application as SecureChatApplication
        ViewModelFactory(app.appContainer.userRepository, app.appContainer.messageRepository, app.appContainer.chatRepository, app.appContainer.twoFactorAuthManager)
    }
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        chatListAdapter = ChatListAdapter(
            onChatClick = { chat ->
                viewModel.onChatClicked(chat)
            },
            onChatLongClick = { chat ->
                showChatOptionsMenu(chat)
            }
        )

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
        }
    }

    private fun setupObservers() {
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            chatListAdapter.submitChats(chats)
            binding.emptyState.visibility = if (chats.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        viewModel.navigateToChat.observe(viewLifecycleOwner) { chatInfo ->
            chatInfo?.let { (chatId, participantHandle) ->
                val action = ChatListFragmentDirections
                    .actionNavChatsToChatDetail(chatId, participantHandle)
                findNavController().navigate(action)
                viewModel.onNavigatedToChat()
            }
        }
    }

    private fun setupClickListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshChats()
        }

        binding.fabNewChat.setOnClickListener {
            showNewChatDialog()
        }
    }

    private fun showNewChatDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter user handle (e.g., @username)"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Start New Chat")
            .setMessage("Enter the handle of the user you want to chat with:")
            .setView(editText)
            .setPositiveButton("Start Chat") { _, _ ->
                val handle = editText.text.toString().trim().removePrefix("@")
                if (handle.isNotEmpty()) {
                    viewModel.startNewChat(handle)
                } else {
                    Snackbar.make(binding.root, "Please enter a valid handle", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showChatOptionsMenu(chat: com.securechat.android.data.model.Chat) {
        val options = arrayOf("Mark as Read", "Delete Chat")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chat Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.markChatAsRead(chat.id)
                    1 -> showDeleteChatConfirmation(chat)
                }
            }
            .show()
    }
    
    private fun showDeleteChatConfirmation(chat: com.securechat.android.data.model.Chat) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Chat")
            .setMessage("Are you sure you want to delete this chat? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteChat(chat)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}