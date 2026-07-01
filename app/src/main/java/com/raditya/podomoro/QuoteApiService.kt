package com.raditya.podomoro

import retrofit2.http.GET

interface QuoteApiService {
    @GET("today")
    suspend fun getQuoteOfDay(): List<QuoteResponse>
}