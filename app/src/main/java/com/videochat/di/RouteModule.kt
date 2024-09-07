package com.videochat.di

import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RouteModule {

    @Provides
    fun provideRouteDestinationToUiMapper(): RouteDestinationToUiMapper = RouteDestinationToUiMapper()
}