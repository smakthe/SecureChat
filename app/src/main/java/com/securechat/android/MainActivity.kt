package com.securechat.android.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.securechat.android.R
import com.securechat.android.SecureChatApplication
import com.securechat.android.databinding.ActivityMainBinding
import com.securechat.android.databinding.NavHeaderMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        val app = application as SecureChatApplication
        ViewModelFactory(app.appContainer.userRepository, app.appContainer.messageRepository, app.appContainer.chatRepository, app.appContainer.twoFactorAuthManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigation()
        setupObservers()
        setupPanicButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBarMain.toolbar)
    }

    private fun setupNavigation() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_chats), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        
        // Handle menu items not in navigation
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_backup -> {
                    Snackbar.make(binding.root, "Backup feature coming soon", Snackbar.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_logout -> {
                    showLogoutConfirmation()
                    true
                }
                else -> {
                    // Let NavController handle other items
                    navController.navigate(menuItem.itemId)
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
        }

        // Setup navigation header
        setupNavigationHeader()
    }

    private fun setupNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)

        viewModel.currentUser.observe(this) { user ->
            user?.let {
                headerBinding.tvUserHandle.text = "@${it.handle}"
                headerBinding.imageView.setImageResource(getAvatarResource(it.avatarId))
            }
        }
    }

    private fun setupObservers() {
        viewModel.panicModeActivated.observe(this) { activated ->
            if (activated) {
                Snackbar.make(
                    binding.root,
                    "Panic mode activated - all chats encrypted",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupPanicButton() {
        binding.appBarMain.fabPanic.setOnClickListener {
            showPanicConfirmationDialog()
        }
    }

    private fun showPanicConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Activate Panic Mode?")
            .setMessage("This will encrypt all your chat history. You'll need your password to decrypt it later.")
            .setPositiveButton("Activate") { _, _ ->
                showPasswordInputDialog()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPasswordInputDialog() {
        val editText = EditText(this)
        editText.hint = "Enter panic password"
        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        
        AlertDialog.Builder(this)
            .setTitle("Panic Password")
            .setMessage("Enter a password to encrypt your chats:")
            .setView(editText)
            .setPositiveButton("Activate") { _, _ ->
                val password = editText.text.toString()
                if (password.isNotEmpty()) {
                    viewModel.activatePanicMode(password)
                } else {
                    Snackbar.make(binding.root, "Password cannot be empty", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout? You'll need to re-authenticate to access your chats.")
            .setPositiveButton("Logout") { _, _ ->
                // Clear user session and restart app
                finishAffinity()
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.drawerLayout.closeDrawers()
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Navigate to search or show search dialog
                Snackbar.make(binding.root, "Search functionality coming soon", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}