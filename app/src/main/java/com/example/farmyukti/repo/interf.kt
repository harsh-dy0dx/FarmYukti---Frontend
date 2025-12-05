package com.example.farmyukti.repo


import com.example.farmyukti.model.MandiRootResponse
import retrofit2.http.GET
import retrofit2.http.Query



private const val RESOURCE_ID = "9ef84268-d588-465a-a308-a864a43d0070"
interface MandiApiService {
    @GET("resource/$RESOURCE_ID")
    suspend fun getMandiPrices(
        // Required for OGD access
        @Query("api-key") apiKey: String,

        // Pagination: how many records to fetch
        @Query("limit") limit: Int= 10,

        // Tells the server to return JSON (as in your curl's query string)
        @Query("format") format: String = "json",

        // Pagination: starting point
        @Query("offset") offset: Int,

        // Optional filter examples (uses the ID from the "field_exposed" list in your JSON)
        @Query("filters[state.keyword]") stateFilter: String? = null

    ): MandiRootResponse
}