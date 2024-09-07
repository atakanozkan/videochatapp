package com.videochat.ui.navigation

import android.os.Bundle
import com.videochat.R
import com.videochat.architecture.presentation.destination.BaseDestination
import com.videochat.architecture.ui.navigation.mapper.DestinationToUiMapper
import com.videochat.architecture.ui.navigation.model.UiDestination
import com.videochat.ui.destination.RouteDestination
import javax.inject.Inject

class RouteDestinationToUiMapper @Inject constructor() : DestinationToUiMapper {
    override fun toUi(presentationDestination: BaseDestination): UiDestination =
        when (presentationDestination) {
            is RouteDestination.Login -> loginUiDestination()
            is RouteDestination.Home -> homeUiDestination()
            is RouteDestination.Register -> registerUiDestination()
            is RouteDestination.StartCall -> startCallUiDestination()
            is RouteDestination.InCall -> inCallUiDestination(presentationDestination)
            else -> throw IllegalArgumentException("Unknown destination: $presentationDestination")
        }

    private fun loginUiDestination() = UiDestination { navController ->
        navController.navigate(R.id.loginFragment)
    }

    private fun homeUiDestination() = UiDestination { navController ->
        navController.navigate(R.id.homeFragment)
    }

    private fun registerUiDestination() = UiDestination { navController ->
        navController.navigate(R.id.registerFragment)
    }

    private fun startCallUiDestination() = UiDestination { navController ->
        navController.navigate(R.id.startCallFragment)
    }

    private fun inCallUiDestination(destination: RouteDestination.InCall) = UiDestination { navController ->
        val bundle = Bundle().apply {
            putString("channelName", destination.channelName)
        }
        navController.navigate(R.id.inCallFragment, bundle)
    }

}
