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
import com.example.campuscircleapp.features.home.fragments.AttendanceFragment
import com.example.campuscircleapp.features.home.fragments.CoursesFragment
import com.example.campuscircleapp.features.home.fragments.DashboardFragment
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class AttendenceActivity : AppCompatActivity() {
    private val homeService = HomeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendence)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val profileImage = findViewById<ShapeableImageView>(R.id.profileImage)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupProfileHeader(profileImage)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_courses -> CoursesFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragmentContainer, it)
                    .commit()
                true
            } ?: false
        }
    }

    private fun setupProfileHeader(profileImage: ShapeableImageView) {
        profileImage.setOnClickListener { showProfileMenu(profileImage) }
        val token = SessionManager.getToken(this)
        if (token.isNullOrBlank()) { navigateToLogin(); return }

        lifecycleScope.launch {
            try {
                val userData = homeService.getUserData(token).data
                findViewById<TextView>(R.id.headerUserName).text = userData.name
                Glide.with(this@AttendenceActivity).load(userData.image).into(profileImage)
            } catch (_: Exception) {}
        }
    }

    private fun showProfileMenu(anchor: ShapeableImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.profile_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_logout) {
                SessionManager.clearToken(this)
                navigateToLogin()
                true
            } else false
        }
        popup.show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}