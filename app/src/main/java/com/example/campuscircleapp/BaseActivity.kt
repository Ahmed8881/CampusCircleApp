package com.example.campuscircleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.campuscircleapp.core.services.signalr.SignalRManager
import com.example.campuscircleapp.core.theme.ThemeManager
import com.example.campuscircleapp.core.utils.DeviceRegistrationHelper
import com.example.campuscircleapp.shared.services.LoaderManager
import com.example.campuscircleapp.shared.services.SessionManager
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        
        val token = SessionManager.getToken(this)
        if (token != null) {
            Log.d("BaseActivity", "User logged in. Initializing services.")
            // Use applicationContext for persistence
            SignalRManager.init(applicationContext)
            
            // We use force = false here to avoid redundant network calls on every activity 
            // BUT we rely on Splash/Login to have done a 'force' sync.
            // If you want to be extra safe against backend deletions, you could trigger 
            // a forced sync here occasionally or based on a timestamp.
            DeviceRegistrationHelper.enqueueRegistration(applicationContext)
        }
    }

    protected fun bindGlobalLoader(@IdRes loaderViewId: Int) {
        val loaderView: View = findViewById(loaderViewId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                LoaderManager.isLoading.collect { isLoading ->
                    loaderView.isVisible = isLoading
                }
            }
        }
    }
}
