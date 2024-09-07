package com.videochat.ui.destination

import com.videochat.architecture.presentation.destination.BaseDestination

sealed class RouteDestination : BaseDestination {
    object Login : RouteDestination()
    object Home : RouteDestination()
    object Register : RouteDestination()
    object StartCall : RouteDestination()
    data class InCall(val channelName: String) : RouteDestination()
}