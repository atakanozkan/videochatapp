package com.videochat.ui.fragment

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Timestamp
import com.videochat.R
import com.videochat.architecture.ui.view.BaseFragment
import com.videochat.data.source.FirestoreSource
import com.videochat.databinding.VideoChatInCallFragmentBinding
import com.videochat.domain.entity.SessionEntity
import com.videochat.presentation.model.UiState
import com.videochat.presentation.viewmodel.UserViewModel
import com.videochat.presentation.viewmodel.VideoChatRoomViewModel
import com.videochat.ui.binder.VideoChatInCallStateBinder
import com.videochat.ui.destination.RouteDestination
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatInCallViewHolder
import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.hilt.android.AndroidEntryPoint
import io.agora.CallBack
import io.agora.ConnectionListener
import io.agora.MessageListener
import io.agora.chat.ChatMessage
import io.agora.chat.TextMessageBody
import io.agora.rtc2.IRtcEngineEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatInCallFragment : BaseFragment<UiState,VideoChatInCallFragmentBinding>(R.layout.video_chat_in_call_fragment) {

    @Inject
    lateinit var fSource: FirestoreSource

    @Inject
    override lateinit var viewModel: VideoChatRoomViewModel

    @Inject
    lateinit var userViewModel: UserViewModel

    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper

    private lateinit var viewHolder: VideoChatInCallViewHolder

    override lateinit var viewStateBinder: VideoChatInCallStateBinder

    private val activityMainScope = CoroutineScope(SupervisorJob())
    private var timeExpirePerform = 4800
    private var opponentUserName: String? = null
    private var callStartTime: Long = 0
    private var callEndTime: Long = 0

    private var fragmentEventListener: FragmentEventListener = object : FragmentEventListener {
        override fun onSuccessEvent() {
            Log.d("FragmentEventListener", "Success event triggered")
        }

        override fun onLoadingEvent() {
            Log.d("FragmentEventListener", "Loading event triggered")
        }

        override fun onErrorEvent() {
            Log.d("FragmentEventListener", "Error event triggered")
        }
    }

    override fun View.bindViews() {
        setupViews()
    }

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): VideoChatInCallFragmentBinding {
        val binding = VideoChatInCallFragmentBinding.inflate(inflater, container, false)
        viewHolder = VideoChatInCallViewHolder(binding.root)
        viewStateBinder = VideoChatInCallStateBinder(viewHolder,fragmentEventListener)
        return binding
    }


    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            activityMainScope.launch {
                callStartTime = System.currentTimeMillis()
                viewModel.setupRemoteVideo(binding, requireContext(), uid)
                opponentUserName = fSource.getUserNameByClientUID(uid)
                withContext(Dispatchers.Main) {
                    binding.tvUsernameInCall.text = opponentUserName
                }
            }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            activityMainScope.launch {
                viewModel._joinState.emit(true)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            activityMainScope.launch {
                callEndTime = System.currentTimeMillis()
                val duration = ((callEndTime - callStartTime) / 1000).toInt()
                logSession(userViewModel.getCurrentUserUID().toString(), uid.toString(), duration)
                withContext(Dispatchers.Main) {
                    binding.tvUsernameInCall.text = "Waiting for user to join..."
                    binding.remoteVideoChatViewInCall.removeAllViews()
                }
                viewModel.resetRemoteVideo(uid)
            }
        }

        override fun onLeaveChannel(stats: RtcStats?) {
            super.onLeaveChannel(stats)
        }
    }

    private fun setupViews() {
        var channelName = arguments?.getString("channelName")
        if (channelName != null) {
            viewModel.setChannelName(channelName)
        }

        userViewModel.updateFromCache()
        viewModel.setAppCredentials(
            agoraAppId = getString(R.string.agora_app_id),
            agoraAppCertificate = getString(R.string.agora_app_certificate)
        )

        val tokenBuilder = com.videochat.common.agora.media.RtcTokenBuilder2()
        val stampValue = (System.currentTimeMillis() / 1000 + timeExpirePerform).toInt()
        val uid = userViewModel.getCurrentUserUID()
        val tokenResult = tokenBuilder.buildTokenWithUid(
            viewModel.myAgoraAppId,
            viewModel.appCertificate,
            viewModel._channelName,
            uid,
            com.videochat.common.agora.media.RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            stampValue,
            stampValue
        )
        viewModel.roomToken = tokenResult

        if (!viewModel.checkVideoPermissions(requireContext())) {
            viewModel.requestVideoPermissions(requireActivity())
        }

        viewModel.initAgoraEngine(requireContext(), rtcEventHandler) { isInitialized ->
            if (isInitialized) {
                viewModel.joinChannel(userViewModel.getCurrentUserUID(), requireContext(), binding)
            } else {
                Log.e("VideoChatInCallFragment", "Failed to initialize RTC engine")
            }
        }

        viewModel.setupChatClient(requireContext(), getString(R.string.chat_app_key))
        setupListeners(requireContext(), binding.messageListInCall)
        viewModel.joinLeaveChat(getString(R.string.chat_client), getString(R.string.chat_client_token))

        binding.btnSendInCall.setOnClickListener {
            val messageContent = binding.etChatMessageInCall.text.toString().trim()
            sendMessage(
                context = requireContext(),
                view = binding.messageListInCall,
                toSendName = getString(R.string.chat_client),
                content = messageContent,
                editMessage = binding.etChatMessageInCall
            )
        }

        binding.btnQuitInCall.setOnClickListener {
            cancelCall()
        }
    }

    private fun setupListeners(context: Context,layout: LinearLayout) {
        viewModel.agoraChatClient?.chatManager()?.addMessageListener(object : MessageListener {
            override fun onMessageReceived(messages: List<ChatMessage>) {
                CoroutineScope(Dispatchers.Main).launch {
                    messages.forEach { message ->
                        val textBody = (message.body as? TextMessageBody)?.message ?: "Unsupported message type"
                        displayMessage(context,layout, textBody, isSentMessage = false)
                    }
                }
            }
        })

        viewModel.agoraChatClient?.addConnectionListener(object : ConnectionListener {
            override fun onConnected() {
                Log.d("AgoraChatQuickStart", "Connected")
            }

            override fun onDisconnected(error: Int) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (viewModel.joinState.value == true) {
                        Log.d("AgoraChatQuickStart", "Disconnected: $error")
                        viewModel._joinState.emit(false)
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



    private fun displayMessage(context: Context, linearLayout: LinearLayout, messageText: String?, isSentMessage: Boolean) {
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

    private fun sendMessage(context: Context, view: View?, toSendName: String, content: String, editMessage: EditText) {
        if (toSendName.isEmpty() || content.isEmpty()) {
            return
        }
        val message = ChatMessage.createTextSendMessage(content, toSendName)
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                CoroutineScope(Dispatchers.Main).launch {
                    displayMessage(context, view as LinearLayout, content, true)
                    editMessage.text.clear()
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(editMessage.applicationWindowToken, 0)
                }
            }
            override fun onError(code: Int, error: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Failed to send message: $error", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.agoraChatClient!!.chatManager().sendMessage(message)
    }

    private fun logSession(callerId: String, receiverId: String, duration: Int) {
        val session = SessionEntity(
            sessionId = UUID.randomUUID().toString(),
            callerId = callerId,
            receiverId = receiverId,
            duration = duration,
            date = Timestamp.now()
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (fSource.insertSession(session)) {
                Log.d("VideoChatInCallFragment", "Session logged successfully")
            } else {
                Log.e("VideoChatInCallFragment", "Failed to log session")
            }
        }
    }
    private fun cancelCall() {
        viewModel.leaveChannel()
        viewModel.destroy()
        navigate(RouteDestination.Home)
    }
}
