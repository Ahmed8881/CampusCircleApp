package com.example.campuscircleapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.campuscircleapp.features.admin.fragments.AdminDashboardFragment
import com.example.campuscircleapp.features.admin.fragments.MarkAttendanceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

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
}
