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
import com.example.campuscircleapp.features.admin.fragments.MarkAttendanceFragment
import com.example.campuscircleapp.features.auth.LoginActivity
import com.example.campuscircleapp.features.home.fragments.SettingsFragment
import com.example.campuscircleapp.features.home.services.HomeService
import com.example.campuscircleapp.features.teacher.fragments.AnnounceAssessmentFragment
import com.example.campuscircleapp.features.teacher.fragments.TeacherCoursesFragment
import com.example.campuscircleapp.features.teacher.fragments.TeacherDashboardFragment
import com.example.campuscircleapp.features.teacher.fragments.TeacherStudentsFragment
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class TeacherActivity : AppCompatActivity() {

    private val homeService = HomeService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.teacherRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            findViewById<BottomNavigationView>(R.id.teacherBottomNavigation)
                .setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.teacherBottomNavigation)
        val profileImage = findViewById<ShapeableImageView>(R.id.teacherProfileImage)

        setupProfileHeader(profileImage)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.teacherFragmentContainer, TeacherDashboardFragment())
                .commit()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_teacher_dashboard -> TeacherDashboardFragment()
                R.id.nav_teacher_courses -> TeacherCoursesFragment()
                R.id.nav_teacher_students -> TeacherStudentsFragment()
                R.id.nav_teacher_attendance -> MarkAttendanceFragment()
                R.id.nav_announce -> AnnounceAssessmentFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.teacherFragmentContainer, it)
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
                findViewById<TextView>(R.id.teacherHeaderUserName).text = userData.name
                Glide.with(this@TeacherActivity)
                    .load(userData.image)
                    .placeholder(R.drawable.logo_2)
                    .error(R.drawable.logo_2)
                    .into(profileImage)
            } catch (_: Exception) {
                findViewById<TextView>(R.id.teacherHeaderUserName).text = "Welcome, Teacher"
            }
        }
    }

    private fun showProfileMenu(anchor: ShapeableImageView) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.profile_menu_teacher, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.teacherFragmentContainer, SettingsFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
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
