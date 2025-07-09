package com.devvaultpro.ui

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable

fun handleOAuthRedirect(activity: ComponentActivity, uri: Uri) {
    val code = uri.getQueryParameter("code")
    if (code != null) {
        Log.d("OAuth", "Code received: $code")
        // Normally send to backend server to exchange for access_token
        // Example:
        // POST /oauth/callback { code: code }
    }
}
