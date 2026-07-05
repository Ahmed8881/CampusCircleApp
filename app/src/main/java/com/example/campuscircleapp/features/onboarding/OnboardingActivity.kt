package com.example.campuscircleapp.features.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.theme.ThemeManager
import com.example.campuscircleapp.features.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    private lateinit var adapter: OnboardingAdapter
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var dots: List<View>

    private val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.logo_web,
            title = "Welcome to Campus Circle",
            description = "Your all-in-one campus management platform. Track attendance, manage courses, and stay connected with your academic community."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_attendance,
            title = "Smart Attendance",
            description = "Mark and track attendance with ease. View your attendance trends, identify patterns, and stay on top of your academic performance."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_trend,
            title = "Insights & Analytics",
            description = "Visualize your academic journey with beautiful charts and detailed analytics. Understand your performance at a glance."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_connect,
            title = "Stay Connected",
            description = "Get real-time announcements, manage your schedule, coordinate with instructors, and never miss an important update."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        pager = findViewById(R.id.onboardingPager)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        dots = listOf<View>(
            findViewById(R.id.dot0),
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        )

        adapter = OnboardingAdapter(this, pages)
        pager.adapter = adapter
        pager.offscreenPageLimit = pages.size

        updateDots(0)

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                btnPrev.visibility = if (position == 0) View.GONE else View.VISIBLE
                btnNext.text = if (position == pages.size - 1) "Start" else "Next"
            }
        })

        btnNext.setOnClickListener {
            val current = pager.currentItem
            if (current < pages.size - 1) {
                pager.currentItem = current + 1
            } else {
                ThemeManager.setOnboardingCompleted(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        btnPrev.setOnClickListener {
            val current = pager.currentItem
            if (current > 0) {
                pager.currentItem = current - 1
            }
        }
    }

    private fun updateDots(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                if (index == position) R.drawable.bg_dot_active
                else R.drawable.bg_dot_inactive
            )
        }
    }
}
