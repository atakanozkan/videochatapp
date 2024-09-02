package com.videochat.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.videochat.common.navigation.NavigationItem
import com.videochat.domain.model.state.UiState
import com.videochat.ui.activity.VideoChatInCallActivity
import com.videochat.ui.activity.VideoChatLoginActivity
import com.videochat.ui.activity.VideoChatMainActivity
import com.videochat.ui.activity.VideoChatRegisterActivity
import com.videochat.ui.activity.VideoChatStartCallActivity

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    lateinit var binding: B

    abstract fun initBinding(): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initBinding()
        setContentView(binding.root)
        setupViews()
    }

    open fun setupViews() {}

    open fun render(uiState: UiState){}

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun navigateTo(item: NavigationItem) {
        val intent = when (item) {
            is NavigationItem.VideoChatLogin -> Intent(this, VideoChatLoginActivity::class.java)
            is NavigationItem.VideoChatRegister -> Intent(this, VideoChatRegisterActivity::class.java)
            is NavigationItem.VideChatMain -> Intent(this, VideoChatMainActivity::class.java)
            is NavigationItem.VideoChatStartCall -> Intent(this, VideoChatStartCallActivity::class.java)
            is NavigationItem.VideoChatInCall -> Intent(this, VideoChatInCallActivity::class.java).apply {
                putExtra("channelName", item.channelName)
            }
        }
        startActivity(intent)
    }

    fun navigateToAndFinish(item: NavigationItem) {
        navigateTo(item)
        finish()
    }


}