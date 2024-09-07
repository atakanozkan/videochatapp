package com.videochat.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.videochat.R
import com.videochat.architecture.ui.view.BaseFragment
import com.videochat.databinding.VideoChatMainFragmentBinding
import com.videochat.presentation.model.UiState
import com.videochat.presentation.viewmodel.AppConfigViewModel
import com.videochat.presentation.viewmodel.UserViewModel
import com.videochat.ui.adapter.SessionAdapter
import com.videochat.ui.binder.VideoChatMainStateBinder
import com.videochat.ui.destination.RouteDestination
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatMainViewHolder
import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatMainFragment() : BaseFragment<UiState,VideoChatMainFragmentBinding>(
    layoutResourceId = R.layout.video_chat_main_fragment
) {

    @Inject
    override lateinit var viewModel: UserViewModel
    @Inject
    lateinit var appConfigViewModel: AppConfigViewModel

    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper

    private lateinit var viewHolder: VideoChatMainViewHolder

    override lateinit var viewStateBinder: VideoChatMainStateBinder

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

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): VideoChatMainFragmentBinding {
        val binding = VideoChatMainFragmentBinding.inflate(inflater, container, false)
        viewHolder = VideoChatMainViewHolder(binding.root)
        viewStateBinder = VideoChatMainStateBinder(viewHolder,fragmentEventListener)
        return binding
    }

    fun setupViews() {
        setupRecyclerView()
        setupSessionHistoryObserver()

        lifecycleScope.launch {
            val update = viewModel.updateFromCache()

            update.join()

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("main user",viewModel.userModel.value.toString())
                viewModel.userModel.observe(viewLifecycleOwner) { userModel ->
                    if (userModel != null) {
                        binding.tvUsernameMain.text = userModel.userName
                    } else {
                        Log.d("user", "User is null or failed to load.")
                    }
                }
            }
        }




        binding.btnAddCallMain.setOnClickListener {
            navigate(RouteDestination.StartCall)
        }

        binding.btnLogoutMain.setOnClickListener {
            viewModel.logoutUser()
            appConfigViewModel.setRememberMe(false)
            navigate(RouteDestination.Login)
        }
    }

    private fun setupRecyclerView() {
        binding.rvCallHistoryMain.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSessionHistoryObserver() {
        viewModel.sessionhistory.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                binding.tvNoCallsMain.visibility = View.VISIBLE
                binding.rvCallHistoryMain.visibility = View.GONE
            } else {
                binding.tvNoCallsMain.visibility = View.GONE
                binding.rvCallHistoryMain.visibility = View.VISIBLE
                binding.rvCallHistoryMain.adapter = SessionAdapter(sessions)
            }
        }
    }

}
