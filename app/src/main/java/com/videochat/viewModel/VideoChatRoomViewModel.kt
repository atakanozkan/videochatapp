package com.videochat.viewModel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.SurfaceView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.videochat.base.BaseViewModel
import com.videochat.databinding.VideoChatInCallBinding
import com.videochat.domain.model.state.UiState
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.MessageListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.ChatOptions
import io.agora.chat.TextMessageBody
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


class VideoChatRoomViewModel : BaseViewModel<UiState>() {
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
    private var agoraChatClient: ChatClient? = null

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
    private fun setupLocalVideo(binding: VideoChatInCallBinding, context: Context) {
        viewModelScope.launch {
            val localSurfaceView = SurfaceView(context).apply {
                setZOrderMediaOverlay(true)
            }
            withContext(Dispatchers.Main){
                binding.localVideoChatView.addView(localSurfaceView)
            }
            rtcEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
        }
    }

    fun setupRemoteVideo(binding: VideoChatInCallBinding, context: Context, uid: Int) {
        viewModelScope.launch {
            val remoteSurfaceView = SurfaceView(context).apply {
                setZOrderMediaOverlay(true)
            }
            withContext(Dispatchers.Main){
                binding.remoteVideoChatView.addView(remoteSurfaceView)
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

    fun setupListeners(context: Context,layout: LinearLayout) {
        agoraChatClient?.chatManager()?.addMessageListener(object : MessageListener {
            override fun onMessageReceived(messages: List<ChatMessage>) {
                CoroutineScope(Dispatchers.Main).launch {
                    messages.forEach { message ->
                        val textBody = (message.body as? TextMessageBody)?.message ?: "Unsupported message type"
                        displayMessage(context,layout, textBody, isSentMessage = false)
                    }
                }
            }
        })

        agoraChatClient?.addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                Log.d("AgoraChatQuickStart", "Connected")
            }

            override fun onDisconnected(error: Int) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (joinState.value == true) {
                        Log.d("AgoraChatQuickStart", "Disconnected: $error")
                        _joinState.emit(false)
                    }
                }
            }

            override fun onLogout(errorCode: Int) {
                Log.d("AgoraChatQuickStart", "User logging out: $errorCode")
            }

            override fun onTokenExpired() {
                Log.d("AgoraChatQuickStart", "Token expired")
            }

            override fun onTokenWillExpire() {
                Log.d("AgoraChatQuickStart", "Token will expire soon")
            }
        })
    }

    fun joinChannel(uid: Int, context: Context, binding: VideoChatInCallBinding) {
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
                    _uiState.value = UiState.Error("Error joining channel: ${e.localizedMessage}")
                }
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(context: Context, view: View?, toSendName: String, content: String,editMessage:EditText) {
        if (toSendName.isEmpty() || content.isEmpty()) {
            return
        }
        val message = ChatMessage.createTextSendMessage(content, toSendName)
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                CoroutineScope(Dispatchers.Main).launch {
                    displayMessage(context, view as LinearLayout, content, true)
                    editMessage.text.clear()
                    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(editMessage.applicationWindowToken, 0)
                }
            }
            override fun onError(code: Int, error: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Failed to send message: $error", Toast.LENGTH_SHORT).show()
                }
            }
        })
        agoraChatClient!!.chatManager().sendMessage(message)
    }


    fun displayMessage(context: Context,linearLayout: LinearLayout,messageText: String?, isSentMessage: Boolean) {
        val messageTextView = TextView(context)
        messageTextView.text = messageText
        messageTextView.setPadding(10, 10, 10, 10)

        val messageList: LinearLayout = linearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (isSentMessage) {
            params.gravity = Gravity.END
            messageTextView.setBackgroundColor(Color.parseColor("white"))
            params.setMargins(100, 25, 15, 5)
            params.setMargins(100, 25, 15, 5)
        } else {
            messageTextView.setBackgroundColor(Color.parseColor("white"))
            params.setMargins(15, 25, 100, 5)
        }
        messageList.addView(messageTextView, params)
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