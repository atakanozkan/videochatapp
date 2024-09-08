package com.videochat.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class VideoChatMainFragment : BaseFragment<UiState,VideoChatMainFragmentBinding>(
    layoutResourceId = R.layout.video_chat_main_fragment
) {

    @Inject
    override lateinit var viewModel: UserViewModel
    @Inject
    lateinit var appConfigViewModel: AppConfigViewModel
    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper
    @Inject
    lateinit var fAuth: FirebaseAuth

    private lateinit var viewHolder: VideoChatMainViewHolder

    override lateinit var viewStateBinder: VideoChatMainStateBinder

    private val timeoutMsForCache = 3000

    private var fragmentEventListener: FragmentEventListener = object : FragmentEventListener {
        override fun onSuccessEvent() {
            Log.d("FragmentEventListener", "Success event triggered")
        }

        override fun onLoadingEvent() {
            Log.d("FragmentEventListener", "Loading event triggered")
        }

        override fun onErrorEvent() {
            Log.d("FragmentEventListener", "Error event triggered")
            logoutUserSession()
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

    override fun setupViews() {
        setupRecyclerView()
        setupSessionHistoryObserver()
        setupButtonListeners()
        setupUserMain()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = viewHolder.rvCallHistory
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SessionAdapter()
    }

    private fun setupSessionHistoryObserver() {
        viewModel.sessionhistory.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                Log.d("sessions","empty")
                binding.tvNoCallsMain.visibility = View.VISIBLE
                binding.rvCallHistoryMain.visibility = View.GONE
            } else {
                binding.tvNoCallsMain.visibility = View.GONE
                binding.rvCallHistoryMain.visibility = View.VISIBLE
                binding.rvCallHistoryMain.adapter = SessionAdapter(sessions)
            }
        }
    }
    @OptIn(FlowPreview::class)
    private fun setupUserMain(){
        lifecycleScope.launch {
            render(UiState.Loading)
            val update = withContext(Dispatchers.IO){
                viewModel.updateFromCache()
            }

            update.join()

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    viewModel.userModel
                        .timeout(timeoutMsForCache.milliseconds)
                        .catch {
                                cause: Throwable ->
                            if(cause is CancellationException){
                                emit(null)
                                throw cause
                            }
                            else {
                                throw cause
                            }
                        }
                        .collect { userModel ->
                            if (userModel != null) {
                                render(UiState.Success)
                                binding.tvUsernameMain.text = userModel.userName
                            }
                        }
                }
                catch (exception: Exception){
                    if(exception is CancellationException){
                        launch {
                            fAuth.uid?.let {
                                val loadCredentialJob = viewModel.loadUserCredentials(it)
                                loadCredentialJob.join()
                                val credentials = viewModel.userCredentials.value
                                if(credentials != null){
                                    val model = viewModel.createUserModel(credentials.userId,credentials.userEmail,credentials.userName,credentials.clientUID)
                                    viewModel.saveUserToCache(credentials.userId,credentials.userName,credentials.userEmail,credentials.passwordHash,credentials.salt,credentials.clientUID)
                                    binding.tvUsernameMain.text = model.userName
                                    render(UiState.Success)
                                }
                                else{
                                    render(UiState.Error("credentials failed"))
                                }
                            }
                        }
                    }
                    else{
                        render(UiState.Error("credentials failed"))
                    }
                }

            }
        }
    }

    private fun setupButtonListeners(){
        binding.btnAddCallMain.setOnClickListener {
            navigate(RouteDestination.StartCall)
        }

        binding.btnLogoutMain.setOnClickListener {
            logoutUserSession()
        }
    }

    private fun logoutUserSession(){
        viewModel.logoutUser()
        appConfigViewModel.setRememberMe(false)
        navigate(RouteDestination.Login)
    }

    private fun render(uiState: UiState) {
        applyViewState(uiState)
    }
}
