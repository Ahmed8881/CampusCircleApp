package com.example.campuscircleapp

import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.campuscircleapp.shared.services.LoaderManager
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {

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