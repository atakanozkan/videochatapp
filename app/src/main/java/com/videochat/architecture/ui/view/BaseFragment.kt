package com.videochat.architecture.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.videochat.architecture.presentation.destination.BaseDestination
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.architecture.ui.navigation.mapper.DestinationToUiMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseFragment<VIEW_STATE : Any,BINDING : ViewBinding> :
    Fragment,
    ViewsProvider {
    constructor() : super()
    constructor(@LayoutRes layoutResourceId: Int) : super(layoutResourceId)

    open val navController: NavController
        get() = findNavController()

    abstract val viewModel: BaseViewModel<VIEW_STATE>

    abstract val viewStateBinder: ViewStateBinder<VIEW_STATE, ViewsProvider>

    abstract val destinationToUiMapper: DestinationToUiMapper

    private var _binding: BINDING? = null
    protected val binding: BINDING
        get() = _binding!!

    private var uiStateJob: Job? = null
    private var destinationJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initializeBinding(inflater, container)
        val view = binding.root

        view.bindViews()
        observeViewModel()
        return view
    }

    abstract fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): BINDING

    abstract fun View.bindViews()

    private fun observeViewModel() {
        uiStateJob = performOnStartedLifecycleEvent {
            viewModel.uiState.collect(::applyViewState)
        }
        destinationJob = performOnStartedLifecycleEvent {
            viewModel.destination.collect(::navigate)
        }
    }

    private fun performOnStartedLifecycleEvent(block: suspend CoroutineScope.() -> Unit): Job? {
        return lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }

    fun applyViewState(viewState: VIEW_STATE) {
        with(viewStateBinder) {
            bindState(viewState)
        }
    }

    fun navigate(destination: BaseDestination) {
        val uiDestination = destinationToUiMapper.toUi(destination)
        uiDestination.navigate(navController)
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiStateJob?.cancel()
        destinationJob?.cancel()
        _binding = null
    }
}
