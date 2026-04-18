package com.example.campuscircleapp

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campuscircleapp.features.auth.LoginActivity
import com.example.campuscircleapp.features.admin.fragments.AdminDashboardFragment
import com.example.campuscircleapp.features.admin.fragments.MarkAttendanceFragment
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private val homeService = HomeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            findViewById<BottomNavigationView>(R.id.adminBottomNavigation)
                .setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.adminBottomNavigation)
        val profileImage = findViewById<ShapeableImageView>(R.id.adminProfileImage)

        setupProfileHeader(profileImage)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.adminFragmentContainer, AdminDashboardFragment())
                .commit()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_admin_dashboard -> AdminDashboardFragment()
                R.id.nav_mark_attendance -> MarkAttendanceFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.adminFragmentContainer, it)
                    .commit()
                true
            } ?: false
        }
    }

    private fun setupProfileHeader(profileImage: ShapeableImageView) {
        profileImage.setOnClickListener {
            showProfileMenu(profileImage)
        }

        val token = SessionManager.getToken(this)
        if (token.isNullOrBlank()) {
            navigateToLogin()
            return
        }

        lifecycleScope.launch {
            try {
                val userData = homeService.getUserData(token).data
                findViewById<TextView>(R.id.adminHeaderUserName).text = userData.name

                Glide.with(this@AdminActivity)
                    .load(userData.image)
                    .placeholder(R.drawable.logo_2)
                    .error(R.drawable.logo_2)
                    .into(profileImage)
            } catch (_: Exception) {
                findViewById<TextView>(R.id.adminHeaderUserName).text = "Welcome"
            }
        }
    }

    private fun showProfileMenu(anchor: ShapeableImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.profile_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    SessionManager.clearToken(this)
                    navigateToLogin()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
