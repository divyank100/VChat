package com.example.vchat.di

import android.content.Context
import com.example.vchat.VChat
import com.example.vchat.api.VChatApi
import com.example.vchat.util.AppConstants
import com.example.vchat.util.PrefHelper
import com.example.vchat.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
    fun provideAuthInterceptor(prefHelper: PrefHelper): Interceptor {
        return Interceptor { chain ->
            val accessToken = prefHelper.getString(AppConstants.accessToken) ?: ""
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken") // Add Authorization header
                .build()
            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        authInterceptor: Interceptor
    ): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client =
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging).build()

        return Retrofit.Builder().baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

    @Singleton
    @Provides
    fun providesApi(retrofit: Retrofit): VChatApi {
        return retrofit.create(VChatApi::class.java)
    }


}