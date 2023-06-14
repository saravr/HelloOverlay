package com.example.hellooverlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*

class OverlayWindow(private val context: Context) {
    private val view: View
    private lateinit var params: WindowManager.LayoutParams
    private val windowManager: WindowManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams( // Shrink the window to wrap the content rather than filling the screen
                400,
                WindowManager.LayoutParams.MATCH_PARENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.popup_window, null)
        view.findViewById<View>(R.id.window_close).setOnClickListener { close() }
        params.gravity = Gravity.END
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        view.findViewById<View>(R.id.btn_move).setOnClickListener {
            if (params.gravity == Gravity.END) {
                params.gravity = Gravity.START
            } else {
                params.gravity = Gravity.END
            }
            this.view.layoutParams = params
            windowManager.updateViewLayout(this.view, params)
        }
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

    private fun close() {
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

    companion object {
        private const val TAG = "Window"
    }
}
