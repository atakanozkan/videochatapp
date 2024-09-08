package com.videochat.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.videochat.R
import com.videochat.presentation.model.SessionPresentationModel

class SessionAdapter(private var sessions: List<SessionPresentationModel>) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    constructor() : this(emptyList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session)
    }

    override fun getItemCount(): Int = sessions.size

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCallerId: TextView = itemView.findViewById(R.id.tvCallerId)
        private val tvReceiverId: TextView = itemView.findViewById(R.id.tvReceiverId)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        @SuppressLint("SetTextI18n")
        fun bind(session: SessionPresentationModel) {
            tvCallerId.text = "Host Id : "+session.callerId
            tvReceiverId.text ="Attendee Id: " + session.receiverId
            tvDuration.text ="Duration : " + session.duration.toString() + " seconds"
            tvDate.text = "Call Date :" +session.date.toDate().toString()
        }
    }
}
