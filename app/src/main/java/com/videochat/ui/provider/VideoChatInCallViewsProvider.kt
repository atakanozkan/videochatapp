package com.videochat.ui.provider

import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.videochat.architecture.ui.binder.ViewsProvider

interface VideoChatInCallViewsProvider : ViewsProvider {
    val progressBar: ProgressBar
    val btnQuit: ImageButton
    val localVideoChatView: FrameLayout
    val remoteVideoChatView: FrameLayout
    val tvUsername: TextView
    val scrollView: ScrollView
    val messageList: LinearLayout
}