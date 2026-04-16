package com.example.farmyukti

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.farmyukti.AppViewModel

import coil.compose.AsyncImage
import com.example.farmyukti.model.WeatherResponse
import kotlinx.coroutines.launch

// --- Composable UI ---

//@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: AppViewModel) {
    val data = viewModel.weatherData
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF6FF))
    ) {
        // --- FIGMA HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF06B6D4))
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Cloud, contentDescription = null, tint = Color.White)
                            }
                        }
                        Column {
                            Text(
                                text = "Weather Advisory",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily forecast & farming tips",
                                color = Color(0xFFDEEBFF),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- YOUR ORIGINAL SEARCH BAR INCORPORATED INTO UI ---
                    OutlinedTextField(
                        value = viewModel.userInputLocation,
                        onValueChange = viewModel::onUserInputLocationChange,
                        placeholder = { Text("Enter city name...", color = Color.White.copy(alpha=0.7f)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.submitManualLocation() }),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                        trailingIcon = {
                            if(isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- ERROR STATE ---
        if (error != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
        }

        // --- REAL CURRENT WEATHER CARD ---
        if (data != null && !isLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF2563EB), Color(0xFF06B6D4))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "📍 ${data.location.name}, ${data.location.country}",
                                        color = Color(0xFFDEEBFF),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${data.current.tempC}°C",
                                        color = Color.White,
                                        fontSize = 56.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = data.current.condition.text,
                                        color = Color(0xFFDEEBFF),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Feels like ${data.current.feelslikeC}°C",
                                        color = Color(0xFFDEEBFF),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Surface(
                                    modifier = Modifier.size(80.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AsyncImage(
                                            model = "https:${data.current.condition.icon}",
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WeatherStat("Humidity", "${data.current.humidity}%", Icons.Default.WaterDrop, Modifier.weight(1f))
                                WeatherStat("Wind", "${data.current.windKph} kph", Icons.Default.Air, Modifier.weight(1f))
                                WeatherStat("Precip", "${data.current.precipMm} mm", Icons.Default.Grain, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        } else if (!isLoading && data == null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Search for a location to view weather", color = Color.Gray)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun WeatherStat(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text(text = label, color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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









private fun createWeatherPrompt(location : String): String {
    return """
        return in hindi language,
        don't return any special character like * or #, and give only formal reply for professional and in short reply to the point reply
        You are an expert agronomist providing detailed, actionable agricultural and weather-based sowing advisories. Base your advice strictly on the current long-range weather forecast for the specified region. Be specific about recommended crops and best practices for the coming 3-4 months.
        Using the current and next 4-month weather forecast for ${location}location, provide expert agricultural advice for optimal sowing, including recommended crops, ideal soil moisture, and expected temperature trends. Structure the response with clear headings."
    """.trimIndent()
}