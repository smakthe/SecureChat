package com.securechat.android.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.securechat.android.data.model.Chat
import com.securechat.android.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(
    private val onChatClick: (Chat) -> Unit,
    private val onChatLongClick: (Chat) -> Unit
) : ListAdapter<ChatListAdapter.ChatWithParticipant, ChatListAdapter.ChatViewHolder>(ChatDiffCallback()) {
    
    data class ChatWithParticipant(
        val chat: Chat,
        val participantHandle: String = "@unknown",
        val participantAvatar: Int = com.securechat.android.R.drawable.avatar_1
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding, onChatClick, onChatLongClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    fun submitChats(chats: List<Chat>) {
        val chatWithParticipants = chats.map { chat ->
            ChatWithParticipant(
                chat = chat,
                participantHandle = "@user${chat.participantId.take(8)}",
                participantAvatar = getAvatarResource(chat.participantId)
            )
        }
        submitList(chatWithParticipants)
    }

    class ChatViewHolder(
        private val binding: ItemChatBinding,
        private val onChatClick: (Chat) -> Unit,
        private val onChatLongClick: (Chat) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        fun bind(chatWithParticipant: ChatWithParticipant) {
            val chat = chatWithParticipant.chat
            binding.apply {
                tvHandle.text = chatWithParticipant.participantHandle
                tvLastMessage.text = chat.lastMessage.ifEmpty { "No messages yet" }

                // Format time
                val calendar = Calendar.getInstance()
                val today = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.timeInMillis

                tvTime.text = when {
                    chat.lastMessageTime > yesterday -> timeFormat.format(Date(chat.lastMessageTime))
                    else -> dateFormat.format(Date(chat.lastMessageTime))
                }

                // Show unread count
                if (chat.unreadCount > 0) {
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                    tvUnreadCount.text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString()
                } else {
                    tvUnreadCount.visibility = android.view.View.GONE
                }

                // Set avatar
                ivAvatar.setImageResource(chatWithParticipant.participantAvatar)

                root.setOnClickListener { onChatClick(chat) }
                root.setOnLongClickListener {
                    onChatLongClick(chat)
                    true
                }
            }
        }


    }

    private fun getAvatarResource(participantId: String): Int {
        val avatarIds = listOf(
            com.securechat.android.R.drawable.avatar_1,
            com.securechat.android.R.drawable.avatar_2,
            com.securechat.android.R.drawable.avatar_3,
            com.securechat.android.R.drawable.avatar_4,
            com.securechat.android.R.drawable.avatar_5
        )
        return avatarIds[Math.abs(participantId.hashCode()) % avatarIds.size]
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatWithParticipant>() {
        override fun areItemsTheSame(oldItem: ChatWithParticipant, newItem: ChatWithParticipant): Boolean {
            return oldItem.chat.id == newItem.chat.id
        }

        override fun areContentsTheSame(oldItem: ChatWithParticipant, newItem: ChatWithParticipant): Boolean {
            return oldItem == newItem
        }
    }
}