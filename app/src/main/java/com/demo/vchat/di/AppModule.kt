package com.demo.vchat.di

import android.content.Context
import com.demo.vchat.util.PrefHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePrefHelper(@ApplicationContext context: Context): PrefHelper {
        return PrefHelper(context)
    }
}