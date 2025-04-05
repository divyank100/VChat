package com.demo.vchat.di

import com.demo.vchat.util.PrefHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefHelperEntryPoint {
    fun getPrefHelper(): PrefHelper
}