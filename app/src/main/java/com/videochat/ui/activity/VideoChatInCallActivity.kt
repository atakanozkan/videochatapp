package com.videochat.ui.activity

import android.util.Log
import androidx.activity.viewModels
import com.google.firebase.Timestamp
import com.videochat.R
import com.videochat.base.BaseActivity
import com.videochat.common.agora.media.RtcTokenBuilder2
import com.videochat.common.navigation.NavigationItem
import com.videochat.data.source.FirestoreSource
import com.videochat.databinding.VideoChatInCallBinding
import com.videochat.domain.entity.session.SessionEntity
import com.videochat.viewModel.UserViewModel
import com.videochat.viewModel.VideoChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.IRtcEngineEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class VideoChatInCallActivity: BaseActivity<VideoChatInCallBinding>() {
    private val chatRoomViewModel: VideoChatRoomViewModel by viewModels()
    @Inject lateinit var fSource: FirestoreSource
    private val userViewModel: UserViewModel by viewModels()
    private val activityMainScope = CoroutineScope(SupervisorJob())
    private var timeExpirePerform = 4800
    private var opponentUserName: String? = null
    private var callStartTime: Long = 0
    private var callEndTime: Long = 0

    override fun initBinding(): VideoChatInCallBinding = VideoChatInCallBinding.inflate(layoutInflater)

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            activityMainScope.launch {
                callStartTime = System.currentTimeMillis()
                chatRoomViewModel.setupRemoteVideo(binding, this@VideoChatInCallActivity, uid)
                opponentUserName = fSource.getUserNameByClientUID(uid)
                withContext(Dispatchers.Main){
                    binding.tvUsername.text = opponentUserName
                }
            }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            activityMainScope.launch {
                chatRoomViewModel._joinState.emit(true)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            activityMainScope.launch{
                callEndTime = System.currentTimeMillis()
                val duration = ((callEndTime - callStartTime) / 1000).toInt()
                logSession(userViewModel.getCurrentUserUID().toString(),uid.toString(),duration)
                withContext((Dispatchers.Main)){
                    binding.tvUsername.text = "Waiting for user to join..."
                    binding.remoteVideoChatView.removeAllViews()
                }
                chatRoomViewModel.resetRemoteVideo(uid)
            }
        }

        override fun onLeaveChannel(stats: RtcStats?) {
            super.onLeaveChannel(stats)
        }
    }

    override fun setupViews() {
        super.setupViews()
        setContentView(binding.root)
        val channelName = intent.getStringExtra("channelName")
        //val agoraConfigModel: AgoraConfigModel? = fSource.getAgoraCredentials()
        if (channelName != null) {
            chatRoomViewModel.setChannelName(channelName)
        }

        userViewModel.updateFromCache()
        chatRoomViewModel.setAppCredentials(
            agoraAppId = getResources().getString(R.string.agora_app_id),
            agoraAppCertificate = getResources().getString(R.string.agora_app_certificate),
        )

        val tokenBuilder = RtcTokenBuilder2()
        val stampValue = (System.currentTimeMillis() / 1000 + timeExpirePerform).toInt()
        val uid = userViewModel.getCurrentUserUID()

        val tokenResult = tokenBuilder.buildTokenWithUid(
            chatRoomViewModel.myAgoraAppId,
            chatRoomViewModel.appCertificate,
            chatRoomViewModel._channelName,
            uid,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            stampValue,
            stampValue
        )
        chatRoomViewModel.roomToken = tokenResult

        if(!chatRoomViewModel.checkVideoPermissions(this)){
            chatRoomViewModel.requestVideoPermissions(this)
        }

        chatRoomViewModel.initAgoraEngine(this, rtcEventHandler) { isInitialized ->
            if (isInitialized) {
                chatRoomViewModel.joinChannel(userViewModel.getCurrentUserUID(), this, binding)
            } else {
                Log.e("VideoChatInCallActivity", "Failed to initialize RTC engine")
            }
        }

        chatRoomViewModel.setupChatClient(this,getResources().getString(R.string.chat_app_key))
        chatRoomViewModel.setupListeners(this, binding.messageList)
        chatRoomViewModel.joinLeaveChat(getResources().getString(R.string.chat_client),getResources().getString(R.string.chat_client_token))

        binding.btnSend.setOnClickListener {
            val messageContent = binding.etChatMessage.text.toString().trim()
            chatRoomViewModel.sendMessage(
                context = this@VideoChatInCallActivity,
                view = binding.messageList,
                toSendName = getResources().getString(R.string.chat_client),
                content = messageContent,
                editMessage = binding.etChatMessage
            )

        }

        binding.btnQuit.setOnClickListener{
            cancelCall()
        }
    }

    private fun cancelCall(){
        chatRoomViewModel.leaveChannel()
        chatRoomViewModel.destroy()
        navigateTo(NavigationItem.VideChatMain)
    }

    fun logSession(callerId: String, receiverId: String, duration: Int) {
        val session = SessionEntity(
            sessionId = UUID.randomUUID().toString(),
            callerId = callerId,
            receiverId = receiverId,
            duration = duration,
            date = Timestamp.now()
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (fSource.insertSession(session)) {
                Log.d("VideoChatInCallActivity", "Session logged successfully")
            } else {
                Log.e("VideoChatInCallActivity", "Failed to log session")
            }
        }
    }


}