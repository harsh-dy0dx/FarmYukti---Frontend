package com.example.farmyukti.repo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class retrofitClient {
}
object RetrofitClient {
    // The OGD Base URL for data access
    private const val BASE_URL = "https://api.data.gov.in/"

    // Lazy initialization of the Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Lazy initialization of the API service interface
    val mandiApiService: MandiApiService by lazy {
        retrofit.create(MandiApiService::class.java)
    }
}

// Singleton object to provide the Retrofit instance
object RetrofitClientWeather {
    private const val BASE_URL = "https://api.weatherapi.com/"

    val weatherService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}