package com.condor.splashbox.network

import com.condor.splashbox.authorization.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
object Networking {
    private val token = AuthRepository.AccessTokenSingleton.accessToken

    private val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(
                CustomHeaderInterceptor("Authorization", "Bearer $token")
            )
            .addNetworkInterceptor(
                    HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    fun providesUnsplashApi(): UnsplashApi = retrofit.create()

}