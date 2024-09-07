package com.videochat.architecture.ui.navigation.model

import androidx.navigation.NavController

fun interface UiDestination {
    fun navigate(navController: NavController)
}
