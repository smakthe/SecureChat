package com.securechat.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter // Will create this adapter next

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            Snackbar.make(it, "Create new chat", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            // TODO: Implement logic to create a new chat based on user handle
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_main) // Placeholder for NavHostFragment if used, will adjust later
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_chats, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Setup RecyclerView
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize with dummy data for now
        val dummyChatList = listOf(
            ChatPreview("User1", "Hello there!"),
            ChatPreview("User2", "How are you doing?"),
            ChatPreview("User3", "See you soon!")
        )
        chatAdapter = ChatAdapter(dummyChatList) // Initialize adapter
        chatRecyclerView.adapter = chatAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

// Dummy data class for chat preview
data class ChatPreview(val handle: String, val lastMessage: String)

// Dummy Adapter for RecyclerView
class ChatAdapter(private val chatList: List<ChatPreview>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val handleTextView: android.widget.TextView = view.findViewById(R.id.textView_chat_handle)
        val lastMessageTextView: android.widget.TextView = view.findViewById(R.id.textView_last_message_preview)
        val avatarImageView: android.widget.ImageView = view.findViewById(R.id.imageView_chat_avatar)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ChatViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_chat_preview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.handleTextView.text = chat.handle
        holder.lastMessageTextView.text = chat.lastMessage
        // Set avatar image (dummy for now)
        holder.avatarImageView.setImageResource(android.R.drawable.sym_def_app_icon)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
