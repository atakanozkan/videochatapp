package com.videochat.ui.holder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.videochat.R
import com.videochat.ui.provider.VideoChatInCallViewsProvider

class VideoChatInCallViewHolder(view: View) : VideoChatInCallViewsProvider {
    override val progressBar: ProgressBar = view.findViewById(R.id.progressBar_in_call)
    override val btnQuit: ImageButton = view.findViewById(R.id.btnQuit_in_call)
    override val localVideoChatView: FrameLayout = view.findViewById(R.id.localVideoChatView_in_call)
    override val remoteVideoChatView: FrameLayout = view.findViewById(R.id.remoteVideoChatView_in_call)
    override val tvUsername: TextView = view.findViewById(R.id.tvUsername_in_call)
    override val scrollView: ScrollView = view.findViewById(R.id.scrollView_in_call)
    override val messageList: LinearLayout = view.findViewById(R.id.messageList_in_call)
}
