package com.videochat.ui.activity

import android.widget.Toast
import com.videochat.base.BaseActivity
import com.videochat.common.navigation.NavigationItem
import com.videochat.databinding.VideoChatStartCallBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoChatStartCallActivity : BaseActivity<VideoChatStartCallBinding>() {
    override fun initBinding(): VideoChatStartCallBinding = VideoChatStartCallBinding.inflate(layoutInflater)
    override fun setupViews() {
        super.setupViews()
        binding.startCallButton.setOnClickListener {
            val channelName = binding.roomNameEditField.text.toString()
            if (channelName.isNotBlank()) {
                if(channelName.length < 6){
                    Toast.makeText(this, "Please enter a valid room name with at least 6 characters!", Toast.LENGTH_SHORT).show()
                }
                else{
                    navigateTo(NavigationItem.VideoChatInCall(channelName = channelName ))
                }
            } else {
                Toast.makeText(this, "Please enter a room name!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnQuit.setOnClickListener{
            navigateTo(NavigationItem.VideChatMain)
        }
    }
}