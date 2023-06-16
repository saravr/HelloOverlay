package com.example.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.flow.Flow


class OverlayWindow(private val context: Context, data: Flow<List<String>>) {
    private val view: View
    private lateinit var params: WindowManager.LayoutParams
    private val windowManager: WindowManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams( // Shrink the window to wrap the content rather than filling the screen
                500,
                WindowManager.LayoutParams.MATCH_PARENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }

        params.gravity = Gravity.END

        view = ComposeView(context)
        val self = this
        view.setContent {
            PopUp(
                data,
                moveClicked = {
                    if (params.gravity == Gravity.END) {
                        params.gravity = Gravity.START
                    } else {
                        params.gravity = Gravity.END
                    }
                    this.view.layoutParams = params
                    self.windowManager.updateViewLayout(this.view, params)
                }, clearClicked = {
                    close()
                }
            )
        }

        val lifecycleOwner = OverlayLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        view.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun open() {
        try {
            // check if the view is already inflated or present in the window
            if (view.windowToken == null) {
                if (view.parent == null) {
                    windowManager.addView(view, params)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    fun close() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            view.invalidate()
            (view.parent as? ViewGroup)?.removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
            val overlayService = Intent(context, OverlayService::class.java)
            context.stopService(overlayService)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    companion object {
        private const val TAG = "OverlayWindow"
    }
}
