package com.example.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

class OverlayWindow(private val context: Context) {
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
            (view.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    @Composable
    fun PopUp(moveClicked: () -> Unit, clearClicked: () -> Unit) {
        Column(modifier = Modifier
            .background(Color.Transparent.copy(alpha = 0.2f))
            .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = {
                    moveClicked()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Move around", tint = Color.White)
                }
                IconButton(onClick = { clearClicked() }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                }
            }
            Text("Hello", modifier = Modifier
                .padding(40.dp)
                .background(Color.Yellow))
        }
    }

    companion object {
        private const val TAG = "OverlayWindow"
    }
}
