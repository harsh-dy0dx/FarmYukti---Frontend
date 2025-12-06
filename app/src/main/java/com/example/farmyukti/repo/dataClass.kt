package com.example.farmyukti.model

import com.google.gson.annotations.SerializedName

// 1. Data Class for a Single Price Record (Mandi details)
data class MandiPriceRecord(
    @SerializedName("state")
    val state: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("market")
    val market: String,
    @SerializedName("commodity")
    val commodity: String,
    @SerializedName("variety")
    val variety: String,
    @SerializedName("grade")
    val grade: String,
    @SerializedName("arrival_date")
    val arrivalDate: String,
    // Note: It's best to handle prices as Double or Int if possible,
    // but based on the JSON example they are strings, so we keep them as String for direct mapping.
    @SerializedName("min_price")
    val minPrice: String,
    @SerializedName("max_price")
    val maxPrice: String,
    @SerializedName("modal_price")
    val modalPrice: String
)

// ---

// 2. Data Class for the Root Response (The top-level object)
data class MandiRootResponse(
    @SerializedName("created")
    val created: Long,
    @SerializedName("updated")
    val updated: Long,
    @SerializedName("created_date")
    val createdDate: String,
    @SerializedName("updated_date")
    val updatedDate: String,
    @SerializedName("active")
    val active: String,
    @SerializedName("index_name")
    val indexName: String,
    @SerializedName("org")
    val organization: List<String>,
    @SerializedName("org_type")
    val orgType: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("visualizable")
    val visualizable: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("limit")
    val limit: String,
    @SerializedName("offset")
    val offset: String,

    // The key part: The list of actual price records
    @SerializedName("records")
    val records: List<MandiPriceRecord>
)


////weather
// Top-level response structure
data class WeatherResponse(
    val location: Location,
    val current: Current
)

// Location data
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

// Current weather data
data class Current(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    val condition: Condition,
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    val uv: Double
)

// Condition details, including the icon URL
data class Condition(
    val text: String,
    val icon: String // This is the URL path for the weather icon, e.g., "//cdn.weatherapi.com/weather/64x64/day/302.png"
)