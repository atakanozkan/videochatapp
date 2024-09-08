package com.videochat.ui.provider

import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.videochat.architecture.ui.binder.ViewsProvider

interface VideoChatMainViewsProvider : ViewsProvider {
    val tvUsername: TextView
    val btnLogout: ImageButton
    val btnAddCall: ImageButton
    val tvNoCalls: TextView
    val rvCallHistory: RecyclerView
    val tvUserRetrieveMessage: TextView
    val progressBar: ProgressBar
}