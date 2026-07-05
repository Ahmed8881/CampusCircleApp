package com.example.campuscircleapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campuscircleapp.core.services.signalr.SignalRManager
import com.example.campuscircleapp.features.auth.LoginActivity
import com.example.campuscircleapp.features.home.fragments.AnnouncementsFragment
import com.example.campuscircleapp.features.home.fragments.DashboardFragment
import com.example.campuscircleapp.features.home.fragments.EnrolledCoursesFragment
import com.example.campuscircleapp.features.home.fragments.EventsFragment
import com.example.campuscircleapp.features.home.fragments.SettingsFragment
import com.example.campuscircleapp.features.home.fragments.SettingsLogoutListener
import com.example.campuscircleapp.features.home.fragments.TimetableFragment
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class AttendenceActivity : BaseActivity(), SettingsLogoutListener {

    override fun onLogoutRequested() {
        SignalRManager.stop()
        SessionManager.clearToken(this)
        navigateToLogin()
    }
    private val homeService = HomeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendence)

        findViewById<ImageView>(R.id.headerNotificationBtn).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AnnouncementsFragment())
                .addToBackStack(null)
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(statusBars.left, statusBars.top, statusBars.right, 0)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val profileImage = findViewById<ShapeableImageView>(R.id.profileImage)

        setupProfileHeader(profileImage)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_enrolled -> EnrolledCoursesFragment()
                R.id.nav_timetable -> TimetableFragment()
                R.id.nav_events -> EventsFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
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
                Glide.with(this@AttendenceActivity)
                    .load(userData.image)
                    .placeholder(R.drawable.logo_2)
                    .error(R.drawable.logo_2)
                    .into(profileImage)
            } catch (_: Exception) {
                findViewById<TextView>(R.id.headerUserName).text = "Welcome"
            }
        }
    }

    private fun showProfileMenu(anchor: ShapeableImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.profile_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    SignalRManager.stop()
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
