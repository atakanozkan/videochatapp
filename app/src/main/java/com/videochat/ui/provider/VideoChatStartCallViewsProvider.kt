package com.videochat.ui.provider

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.videochat.architecture.ui.binder.ViewsProvider

interface VideoChatStartCallViewsProvider : ViewsProvider {
    val progressBar: ProgressBar
    val cardView: CardView
    val btnQuit: ImageButton
    val tvStartCall: TextView
    val roomNameEditField: EditText
    val startCallButton: Button
}