package com.securechat.android.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.securechat.android.data.model.ChatMessage
import com.securechat.android.databinding.ItemMessageReceivedBinding
import com.securechat.android.databinding.ItemMessageSentBinding
import com.securechat.android.ui.chat.ChatDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val viewModel: ChatDetailViewModel
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        val currentUserId = viewModel.currentUser.value?.id
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SentMessageViewHolder(binding)
            }
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceivedMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    inner class SentMessageViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                // Decrypt and display message
                viewModel.viewModelScope.launch {
                    if (message.messageType == com.securechat.android.data.model.MessageType.FILE) {
                        tvMessage.text = "📎 ${message.filePath?.substringAfterLast('/') ?: "File"}"
                        if (message.fileSize != null) {
                            tvMessage.append(" (${formatFileSize(message.fileSize)})") 
                        }
                    } else {
                        val decryptedContent = viewModel.decryptMessage(message)
                        tvMessage.text = decryptedContent
                    }
                }

                tvTime.text = timeFormat.format(Date(message.timestamp))
            }
        }
    }

    inner class ReceivedMessageViewHolder(
        private val binding: ItemMessageReceivedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                // Decrypt and display message
                viewModel.viewModelScope.launch {
                    if (message.messageType == com.securechat.android.data.model.MessageType.FILE) {
                        tvMessage.text = "📎 ${message.filePath?.substringAfterLast('/') ?: "File"}"
                        if (message.fileSize != null) {
                            tvMessage.append(" (${formatFileSize(message.fileSize)})") 
                        }
                    } else {
                        val decryptedContent = viewModel.decryptMessage(message)
                        tvMessage.text = decryptedContent
                    }
                }

                tvTime.text = timeFormat.format(Date(message.timestamp))
            }
        }
    }

    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format("%.1f MB", mb)
            kb >= 1 -> String.format("%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}