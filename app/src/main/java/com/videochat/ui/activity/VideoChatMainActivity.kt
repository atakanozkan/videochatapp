package com.videochat.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.videochat.base.BaseActivity
import com.videochat.common.navigation.NavigationItem
import com.videochat.databinding.VideoChatMainBinding
import com.videochat.ui.adapter.SessionAdapter
import com.videochat.viewModel.AppConfigViewModel
import com.videochat.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoChatMainActivity : BaseActivity<VideoChatMainBinding>() {

    private val userViewModel: UserViewModel by viewModels()
    private val appConfigViewModel: AppConfigViewModel by viewModels()

    override fun initBinding(): VideoChatMainBinding = VideoChatMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val myRecycler = binding.rvCallHistory
        myRecycler.setLayoutManager(LinearLayoutManager(this))
        setupViews()
    }

    override fun setupViews() {
        super.setupViews()
        setupSessionHistoryObserver()
        userViewModel.updateFromCache()
        val myRecycler = binding.rvCallHistory
        myRecycler.layoutManager = LinearLayoutManager(this)

        userViewModel.userModel.observe(this) { userModel ->
            if (userModel != null) {
                binding.tvUsername.text = userModel.userName
            }
        }

        binding.btnAddCall.setOnClickListener {
            navigateToAndFinish(NavigationItem.VideoChatStartCall)
        }

        binding.btnLogout.setOnClickListener {
            userViewModel.logoutUser()
            appConfigViewModel.setRememberMe(false)
            navigateToAndFinish(NavigationItem.VideoChatLogin)
        }
    }

    private fun setupSessionHistoryObserver() {
        userViewModel.sessionhistory.observe(this) { sessions ->
            if (sessions.isEmpty()) {
                binding.tvNoCalls.visibility = View.VISIBLE
                binding.rvCallHistory.visibility = View.GONE
            } else {
                binding.tvNoCalls.visibility = View.GONE
                binding.rvCallHistory.visibility = View.VISIBLE
                binding.rvCallHistory.adapter = SessionAdapter(sessions)
            }
        }
    }

}