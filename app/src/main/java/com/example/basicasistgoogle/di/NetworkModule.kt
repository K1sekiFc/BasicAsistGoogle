package com.example.basicasistgoogle.di


import com.example.basicasistgoogle.data.network.api.AttendanceApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideAttendanceApiService(okHttpClient: OkHttpClient): AttendanceApiService {
        val scriptDeploymentId = "AKfycbywELa-gHolBHOpHTvK5NXgkfpq4scxZC9CER67pvkns_UxfVy6cl7PcVpkXKak_Xw"
        val baseUrl = "https://script.google.com/macros/s/$scriptDeploymentId/"


        val jsonConfig = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(AttendanceApiService::class.java)
    }
}
