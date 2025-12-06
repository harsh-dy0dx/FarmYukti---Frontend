package com.example.farmyukti

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import coil.compose.AsyncImage // Coil dependency is assumed for image loading
import com.example.farmyukti.model.WeatherResponse
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

// --- Composable UI ---

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    // ViewModel is now defined in WeatherViewModel.kt
    val viewModel: AppViewModel = viewModel()
    val context = LocalContext.current

    // Accompanist for runtime permission requests
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Compose Weather App") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Location Input and Options ---
            LocationInputSection(
                viewModel = viewModel,
                context = context,
                permissionState = permissionState
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(24.dp))

            // --- State Display (Loading, Error, Success) ---
            WeatherStateDisplay(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationInputSection(
    viewModel: AppViewModel,
    context: Context,
    permissionState: com.google.accompanist.permissions.MultiplePermissionsState
) {
    // --- 1. Auto-Detection Buttons ---


    Spacer(modifier = Modifier.height(24.dp))

    Spacer(modifier = Modifier.height(8.dp))

    // --- 2. Manual Location Input ---
    OutlinedTextField(
        value = viewModel.userInputLocation,
        onValueChange = viewModel::onUserInputLocationChange,
        label = { Text("Enter City/Zip/Postcode") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { viewModel.submitManualLocation() }
        ),
        enabled = !viewModel.isLoading,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )

    Button(
        onClick = viewModel::submitManualLocation,
        enabled = viewModel.userInputLocation.isNotBlank() && !viewModel.isLoading,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text("Search Weather")
    }
}

@Composable
fun WeatherStateDisplay(viewModel: AppViewModel) {

    when {
        // 1. Loading State
        viewModel.isLoading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text("Loading weather data...", modifier = Modifier.padding(top = 16.dp))
            }
        }

        // 2. Error State
        viewModel.error != null -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Error Fetching Data", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(viewModel.error ?: "Unknown error occurred.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Query: ${viewModel.activeLocationQuery}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // 3. Success State
        viewModel.weatherData != null -> {
            WeatherCard(viewModel.weatherData!!)
        }
    }
}

@Composable
fun WeatherCard(data: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Header
            Text(
                text = data.location.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${data.location.region}, ${data.location.country}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Weather Icon and Condition
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                // Coil is assumed to be installed for AsyncImage
                AsyncImage(
                    model = "https:${data.current.condition.icon}",
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${data.current.tempC}°C",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp)
                    )
                    Text(
                        text = "Feels like: ${data.current.feelslikeC}°C",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Text(
                text = data.current.condition.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DetailItem("Humidity", "${data.current.humidity}%")
                DetailItem("Wind", "${data.current.windKph} kph")
                DetailItem("Precip", "${data.current.precipMm} mm")
                DetailItem("UV Index", "${data.current.uv}")
            }

            // Time Stamp
            Text(
                text = "Last updated: ${data.location.localtime}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 20.dp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriAdvisoryAppContainer() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advisery Pro", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16A34A), // Deep Green
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Grass,
                        contentDescription = "App Icon",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        // The core feature is placed within the Scaffold content area
        SowingAdvisoryFeature(Modifier.padding(paddingValues))
    }
}


/**
 * The feature function containing the logic for the Sowing Advisory screen.
 * This is the component that handles the user input, API call, and result display.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SowingAdvisoryFeature(modifier: Modifier = Modifier) {
    // State management using Compose's mutableStateOf
    var location by remember { mutableStateOf("") }
    var advisoryResult by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var loading1 by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Coroutine scope for running suspend functions (like the API call)
    val coroutineScope = rememberCoroutineScope()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    val viewModel : AppViewModel = viewModel()


    val getSowingAdvisory: () -> Unit = {
        if (viewModel.userInputLocation.isEmpty()) {
            error = "Please enter a location."
        } else {
            loading = true
            error = null
            advisoryResult = null

            coroutineScope.launch {
                try {
                    // Call the function with the implemented network logic
                    val prompt = createWeatherPrompt(location)

                    val result = makeGeminiApiCall(GEMINI_API_KEY, prompt)
                    advisoryResult = result
                } catch (e: Exception) {
                    error = "Failed to fetch advisory: ${e.message}"
                    println("API Error: ${e.stackTraceToString()}")
                } finally {
                    loading = false
                }
            }
        }
    }



    val getWeatherCondition: () -> Unit = {
        if (viewModel.userInputLocation.isEmpty()) {
            error = "Please enter a location."
        } else {
            loading1 = true
            error = null
            advisoryResult = null

            coroutineScope.launch {
                try {
                    // Call the function with the implemented network logic

                    viewModel.submitManualLocation()

                } catch (e: Exception) {
                    error = "Failed to fetch Weather Condition: ${e.message}"
                    println("API Error: ${e.stackTraceToString()}")
                } finally {
                    loading1 = false
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)) // bg-gray-50
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .widthIn(max = 800.dp), // max-w-4xl equivalent
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Feature Header
                    Text(
                        text = "Analyze weather conditions for optimal crop planning.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Input Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.userInputLocation,
                            onValueChange = viewModel::onUserInputLocationChange,
                            label = { Text("Location") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { viewModel.submitManualLocation() }
                            ),
                            //modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !loading,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        Button(
                            onClick = getSowingAdvisory,
                            enabled = viewModel.userInputLocation.isNotBlank() && !viewModel.isLoading,
                            modifier = Modifier
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF16A34A) // bg-green-600
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(if (loading) "Analyzing..." else "Get Sowing Advice", fontWeight = FontWeight.SemiBold)
                        }






                        //Button for weather
                        Button(
                            onClick =  getWeatherCondition,
                            enabled = viewModel.userInputLocation.isNotBlank() && !viewModel.isLoading,
                            modifier = Modifier
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF16A34A) // bg-green-600
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(if (loading) "Analyzing..." else "Get Weather Condition", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Status/Error Message
                    error?.let {
                        Text(
                            text = "Error: $it",
                            color = Color(0xFFB91C1C), // text-red-700
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp)) // bg-red-100
                                .border(1.dp, Color(0xFFF87171), RoundedCornerShape(8.dp)) // border-red-400
                                .padding(12.dp)
                        )
                    }

                    // Initial Instruction Message
                    if (advisoryResult == null && !loading && error == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp)) // bg-green-50
                                .border(4.dp, Color(0xFF4ADE80), RoundedCornerShape(8.dp)) // border-green-400
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Enter a location above and click 'Get Sowing Advice'. The AI agronomist will analyze the long-range weather forecast (3-4 months) using real-time search data to give you the best crop recommendations and sowing practices.",
                                fontSize = 16.sp,
                                color = Color(0xFF065F46) // text-green-800
                            )
                        }
                    }

                    // Advisory Output
                    advisoryResult?.let { result ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Expert Advisory for ${location}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Simple display for the text content
                            Text(text = result, color = Color.Black.copy(alpha = 0.8f))
                            // 3. Success State


                            // Citation Sources

                        }
                    }

                    WeatherStateDisplay(viewModel)
                }
            }
        }
    }
}







private fun createWeatherPrompt(location : String): String {
    return """
        return in hindi language,
        don't return any special character like * or #, and give only formal reply for professional and in short reply to the point reply
        You are an expert agronomist providing detailed, actionable agricultural and weather-based sowing advisories. Base your advice strictly on the current long-range weather forecast for the specified region. Be specific about recommended crops and best practices for the coming 3-4 months.
        Using the current and next 4-month weather forecast for ${location}location, provide expert agricultural advice for optimal sowing, including recommended crops, ideal soil moisture, and expected temperature trends. Structure the response with clear headings."
    """.trimIndent()
}