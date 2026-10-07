package com.cacountdown.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import java.time.ZoneId
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Every countdown is read through this single injected Clock, pinned to
     * IST regardless of the device's own timezone setting, so "today" always
     * matches the Indian exam calendar (spec: correct day counts, no
     * timezone bugs).
     */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.system(ZoneId.of("Asia/Kolkata"))
}
