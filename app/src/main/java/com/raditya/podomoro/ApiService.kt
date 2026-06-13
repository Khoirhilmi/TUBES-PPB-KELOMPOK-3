package com.raditya.podomoro

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("advice")
    suspend fun getRandomAdvice(): Response<AdviceResponse>
}
