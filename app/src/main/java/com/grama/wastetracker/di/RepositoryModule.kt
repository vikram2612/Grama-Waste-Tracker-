package com.grama.wastetracker.di

import com.grama.wastetracker.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTractorRepository(impl: TractorRepositoryImpl): TractorRepository

    @Binds
    @Singleton
    abstract fun bindBlackspotRepository(impl: BlackspotRepositoryImpl): BlackspotRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindWasteGuideRepository(impl: WasteGuideRepositoryImpl): WasteGuideRepository
}
