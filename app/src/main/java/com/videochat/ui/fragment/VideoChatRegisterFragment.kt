package com.videochat.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.videochat.R
import com.videochat.architecture.ui.view.BaseFragment
import com.videochat.databinding.VideoChatRegisterFragmentBinding
import com.videochat.presentation.model.UiState
import com.videochat.presentation.viewmodel.VideoChatRegisterViewModel
import com.videochat.ui.binder.VideoChatRegisterStateBinder
import com.videochat.ui.destination.RouteDestination
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatRegisterViewHolder
import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatRegisterFragment : BaseFragment<UiState,VideoChatRegisterFragmentBinding>(
    layoutResourceId = R.layout.video_chat_register_fragment
) {
    @Inject
    override lateinit var viewModel: VideoChatRegisterViewModel

    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper

    private lateinit var viewHolder: VideoChatRegisterViewHolder

    override lateinit var viewStateBinder: VideoChatRegisterStateBinder

    private var fragmentEventListener: FragmentEventListener = object : FragmentEventListener {
        override fun onSuccessEvent() {
            Log.d("FragmentEventListener", "Success event triggered")
            showToast("Account is created successfully!")
            navigate(RouteDestination.Login)
        }

        override fun onLoadingEvent() {
            Log.d("FragmentEventListener", "Loading event triggered")
        }

        override fun onErrorEvent() {
            Log.d("FragmentEventListener", "Error event triggered")
            showToast("Register is failed!")
        }
    }

    override fun View.bindViews() {
        setupViews()
    }

    override fun setupViews(){
        setupRegisterButtons()
    }

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): VideoChatRegisterFragmentBinding {
        val binding = VideoChatRegisterFragmentBinding.inflate(inflater, container, false)
        viewHolder = VideoChatRegisterViewHolder(binding.root)
        viewStateBinder = VideoChatRegisterStateBinder(viewHolder,fragmentEventListener)
        return binding
    }

    private fun setupRegisterButtons() {
        binding.btnRegisterRegister.setOnClickListener {
            val email = binding.emailTextFieldRegister.editText?.text.toString()
            val username = binding.userNameTextFieldRegister.editText?.text.toString()
            val password = binding.passwordTextFieldRegister.editText?.text.toString()
            viewModel.registerUser(email, username, password)
        }
        binding.btnBackLoginRegister.setOnClickListener {
            navigate(RouteDestination.Login)
        }
    }
}
