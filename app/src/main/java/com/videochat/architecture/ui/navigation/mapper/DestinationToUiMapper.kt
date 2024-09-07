package com.videochat.architecture.ui.navigation.mapper

import com.videochat.architecture.presentation.destination.BaseDestination
import com.videochat.architecture.ui.navigation.model.UiDestination

interface DestinationToUiMapper {
    fun toUi(presentationDestination: BaseDestination): UiDestination
}
