package com.videochat.presentation.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.databinding.VideoChatInCallFragmentBinding
import com.videochat.presentation.model.UiState
import io.agora.CallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoChatRoomViewModel(
)  : BaseViewModel<UiState>(UiState.NoChange) {
    private var rtcEngine: RtcEngine? = null
    var myAgoraAppId:  String? = null
    var _channelName:  String? = null
    var appCertificate:  String? = null
    var roomToken:   String? = null
    private val requiredPermissionId = 22
    private val requestedPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    var agoraChatClient: ChatClient? = null

    val _joinState = MutableStateFlow<Boolean?>(null)
    val joinState = _joinState.asStateFlow()

    fun initAgoraEngine(context: Context, rtcEventHandler: IRtcEngineEventHandler, onInitialized: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val config = RtcEngineConfig().apply {
                    mAppId = myAgoraAppId
                    mContext = context
                    mEventHandler = rtcEventHandler
                }
                RtcEngine.create(config)?.let {
                    rtcEngine = it
                    it.enableVideo()
                    onInitialized(true)
                } ?: run {
                    onInitialized(false)
                }
            } catch (e: Exception) {
                onInitialized(false)
            }
        }
    }
    private fun setupLocalVideo(binding: VideoChatInCallFragmentBinding, context: Context) {
        viewModelScope.launch {
            val localSurfaceView = SurfaceView(context).apply {
                setZOrderMediaOverlay(true)
            }
            withContext(Dispatchers.Main){
                binding.localVideoChatViewInCall.addView(localSurfaceView)
            }
            rtcEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
        }
    }

    fun setupRemoteVideo(binding: VideoChatInCallFragmentBinding, context: Context, uid: Int) {
        viewModelScope.launch {
            val remoteSurfaceView = SurfaceView(context).apply {
                setZOrderMediaOverlay(true)
            }
            withContext(Dispatchers.Main){
                binding.remoteVideoChatViewInCall.addView(remoteSurfaceView)
            }
            rtcEngine?.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        }
    }


    fun checkVideoPermissions(context: Context): Boolean = requestedPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun requestVideoPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, requestedPermissions, requiredPermissionId)
    }

    fun setChannelName(channel: String) {
        _channelName = channel
    }

    fun setAppCredentials(agoraAppId: String,agoraAppCertificate: String){
        myAgoraAppId =agoraAppId
        appCertificate = agoraAppCertificate
    }

    fun setupChatClient(context: Context,appkey:String) {
        val options = ChatOptions()
        if (appkey.isEmpty()) {
            return
        }
        options.setAppKey(appkey)
        val localAgoraChatClient = ChatClient.getInstance()
        localAgoraChatClient.init(context, options)
        localAgoraChatClient.setDebugMode(true)
        agoraChatClient = localAgoraChatClient
    }
    fun joinChannel(uid: Int, context: Context, binding: VideoChatInCallFragmentBinding) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkVideoPermissions(context)) {
                    withContext(Dispatchers.Main) {
                        setupLocalVideo(binding, context)
                    }
                    rtcEngine?.apply {
                        startPreview()
                        joinChannel(roomToken, _channelName, uid, ChannelMediaOptions().apply {
                            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
                            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                        })
                    } ?: Log.e("VideoChatRoomViewModel", "RTC Engine is null")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.emit(UiState.Error("Error joining channel: ${e.localizedMessage}"))
                }
                e.printStackTrace()
            }
        }
    }

    fun joinLeaveChat(userName: String,agoraToken: String) {
        viewModelScope.launch {
            if (joinState.value == true) {
                agoraChatClient?.logout(true, object : CallBack {
                    override fun onSuccess() {
                        CoroutineScope(Dispatchers.Main).launch {
                            _joinState.emit(false)
                        }
                    }

                    override fun onError(code: Int, error: String) {
                        Log.e("AgoraChatQuickStart", error)
                    }
                })
            } else {
                agoraChatClient?.loginWithAgoraToken(userName, agoraToken, object : CallBack {
                    override fun onSuccess() {
                        CoroutineScope(Dispatchers.Main).launch {
                            _joinState.emit(false)
                        }
                    }

                    override fun onError(code: Int, error: String) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (code == 200) {
                                _joinState.emit(false)
                            } else {
                                Log.e("AgoraChatQuickStart", error)
                            }
                        }
                    }
                })
            }
        }

    }

    fun leaveChannel() {
        viewModelScope.launch {
            if(joinState.value == true){
                rtcEngine?.leaveChannel()
                _joinState.emit(false)
            }
        }
    }

    fun resetRemoteVideo(uid: Int) {
        rtcEngine?.let {
            it.setupRemoteVideo(VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        }
    }

    fun destroy() {
        RtcEngine.destroy()
        rtcEngine = null
    }

    override fun onCleared() {
        super.onCleared()
        leaveChannel()
        destroy()
    }
}