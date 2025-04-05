package com.demo.vchat.di

import com.demo.vchat.api.VChatApi
import com.demo.vchat.util.AppConstants
import com.demo.vchat.util.PrefHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

        return Retrofit.Builder().baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

    @Singleton
    @Provides
    fun providesApi(retrofit: Retrofit): VChatApi {
        return retrofit.create(VChatApi::class.java)
    }


}