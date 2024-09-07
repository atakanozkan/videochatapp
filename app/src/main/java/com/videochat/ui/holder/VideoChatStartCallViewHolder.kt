package com.videochat.ui.holder

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.videochat.R
import com.videochat.ui.provider.VideoChatStartCallViewsProvider

class VideoChatStartCallViewHolder(view: View) : VideoChatStartCallViewsProvider {
    override val progressBar: ProgressBar = view.findViewById(R.id.progressBar_start_call)
    override val cardView: CardView = view.findViewById(R.id.cardView_start_call)
    override val btnQuit: ImageButton = view.findViewById(R.id.btnQuit_start_call)
    override val tvStartCall: TextView = view.findViewById(R.id.tvStartCall_start_call)
    override val roomNameEditField: EditText = view.findViewById(R.id.roomNameEditField_start_call)
    override val startCallButton: Button = view.findViewById(R.id.startCallButton_start_call)
}
