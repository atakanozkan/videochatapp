package com.videochat.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.videochat.R
import com.videochat.architecture.ui.view.BaseFragment
import com.videochat.databinding.VideoChatStartCallFragmentBinding
import com.videochat.presentation.model.UiState
import com.videochat.presentation.viewmodel.UserViewModel
import com.videochat.ui.binder.VideoChatStartCallStateBinder
import com.videochat.ui.destination.RouteDestination
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatStartCallViewHolder
import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatStartCallFragment : BaseFragment<UiState,VideoChatStartCallFragmentBinding>(
    layoutResourceId = R.layout.video_chat_start_call_fragment
) {
    override val viewModel:  UserViewModel by viewModels()

    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper

    private lateinit var viewHolder: VideoChatStartCallViewHolder

    override lateinit var viewStateBinder: VideoChatStartCallStateBinder

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

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): VideoChatStartCallFragmentBinding {
        val binding = VideoChatStartCallFragmentBinding.inflate(inflater, container, false)
        viewHolder = VideoChatStartCallViewHolder(binding.root)
        viewStateBinder = VideoChatStartCallStateBinder(viewHolder,fragmentEventListener)
        return binding
    }

    override fun setupViews() {
        binding.startCallButtonStartCall.setOnClickListener {
            val channelName = binding.roomNameEditFieldStartCall.text.toString()
            if (channelName.isNotBlank()) {
                if (channelName.length < 6) {
                    Toast.makeText(requireContext(), "Please enter a valid room name with at least 6 characters!", Toast.LENGTH_SHORT).show()
                } else {
                    navigate(RouteDestination.InCall(channelName))
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a room name!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnQuitStartCall.setOnClickListener {
            navigate(RouteDestination.Home)
        }
    }
}
