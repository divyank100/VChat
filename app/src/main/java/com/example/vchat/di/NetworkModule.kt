package com.example.vchat.di

import com.example.vchat.api.VChatApi
import com.example.vchat.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder().baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

    @Singleton
    @Provides
    fun providesApi(retrofit: Retrofit): VChatApi {
        return retrofit.create(VChatApi::class.java)
    }
}