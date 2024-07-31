package com.example.chattingApp.ui.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastUtil {

    private var currentToast: Toast? = null

    fun shortToast(context: Context, text: String) {
        if (currentToast != null) return
        currentToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_SHORT)
        currentToast?.show()
        Handler(Looper.getMainLooper()).postDelayed({ currentToast = null }, 3000)
    }

    fun longToast(context: Context, text: String) {
        if (currentToast != null) return
        currentToast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG)
        currentToast?.show()
        Handler(Looper.getMainLooper()).postDelayed({ currentToast = null }, 5000)
    }
}