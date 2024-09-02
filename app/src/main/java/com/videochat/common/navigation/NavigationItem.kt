package com.videochat.common.navigation

sealed class NavigationItem {
    object VideoChatLogin : NavigationItem()
    object VideoChatRegister : NavigationItem()
    object VideChatMain : NavigationItem()
    object VideoChatStartCall : NavigationItem()
    data class VideoChatInCall(val channelName: String) : NavigationItem()
}
