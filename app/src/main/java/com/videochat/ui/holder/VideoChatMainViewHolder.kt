package com.videochat.ui.holder

import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.videochat.R
import com.videochat.ui.provider.VideoChatMainViewsProvider

class VideoChatMainViewHolder(view: View) : VideoChatMainViewsProvider {
    override val tvUsername: TextView = view.findViewById(R.id.tvUsername_main)
    override val btnLogout: ImageButton = view.findViewById(R.id.btnLogout_main)
    override val btnAddCall: ImageButton = view.findViewById(R.id.btnAddCall_main)
    override val tvNoCalls: TextView = view.findViewById(R.id.tvNoCalls_main)
    override val tvUserRetrieveMessage: TextView = view.findViewById(R.id.tvUserRetrieveMessage_main)
    override val rvCallHistory: RecyclerView = view.findViewById(R.id.rvCallHistory_main)
    override val progressBar: ProgressBar = view.findViewById(R.id.progressBar_main)
}
