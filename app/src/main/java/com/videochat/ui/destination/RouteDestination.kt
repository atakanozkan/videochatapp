package com.videochat.ui.destination

import com.videochat.architecture.presentation.destination.BaseDestination

sealed class RouteDestination : BaseDestination {
    data object Login : RouteDestination()
    data object Home : RouteDestination()
    data object Register : RouteDestination()
    data object StartCall : RouteDestination()
    data class InCall(val channelName: String) : RouteDestination()
}