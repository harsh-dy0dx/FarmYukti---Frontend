package com.example.farmyukti.repo

import android.util.Log
import com.example.farmyukti.model.MandiPriceRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// --- ORIGINAL VIEWMODEL (100% UNTOUCHED) ---

class MandiViewModel : ViewModel() {

    private val OGD_API_KEY = "579b464db66ec23bdd000001a96a53bb28f74a446f081aac57df6102"

    private val _mandiRecords = MutableStateFlow<List<MandiPriceRecord>>(emptyList())
    val mandiRecords: StateFlow<List<MandiPriceRecord>> = _mandiRecords

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchMandiData()
    }

    fun fetchMandiData(page: Int = 0, state: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val pageSize = 10
                val offsetValue = page * pageSize

                val response = RetrofitClient.mandiApiService.getMandiPrices(
                    apiKey = OGD_API_KEY,
                    limit = pageSize,
                    offset = offsetValue,
                    stateFilter = state,
                    format = "json"
                )

                _mandiRecords.value = response.records

            } catch (e: Exception) {
                println("Network Error: Failed to fetch Mandi data. ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// --- UPGRADED FIGMA UI CONNECTED TO VIEWMODEL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiScreen(
    viewModel: MandiViewModel = viewModel(),
    navController: NavController
) {
    val records by viewModel.mandiRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Real-time search filter logic using your actual API records
    val filteredRecords = remember(records, searchQuery) {
        if (searchQuery.isBlank()) records
        else records.filter {
            it.commodity.contains(searchQuery, ignoreCase = true) ||
                    it.market.contains(searchQuery, ignoreCase = true) ||
                    it.district.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7ED))
    ) {
        // --- FIGMA GRADIENT HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFEA580C), Color(0xFFF59E0B))
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
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White)
                            }
                        }
                        Text(
                            text = "Mandi Prices",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live Government Market Rates",
                        color = Color(0xFFFFEDD5),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SEARCH BAR ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search crop or market...", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- LOADING & DATA DISPLAY ---
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFEA580C))
                }
            }
        } else if (filteredRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matching crops found." else "No Mandi price data available.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Live data fed into the Figma cards
            items(filteredRecords) { record ->
                MandiItemCardFigma(record)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- INFO BANNER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEDD5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFEA580C))
                    Column {
                        Text(
                            text = "Note:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A3412)
                        )
                        Text(
                            text = "Rates are pulled directly from the Open Government Data (OGD) Platform in real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9A3412)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

// --- UPGRADED FIGMA CARD DESIGN ---

@Composable
fun MandiItemCardFigma(record: MandiPriceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${record.commodity} (${record.variety})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Market: ${record.market}, ${record.district}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${record.modalPrice}/quintal",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4A7C59),
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                color = Color(0xFFDCFCE7),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = record.arrivalDate,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF15803D)
                    )
                }
            }
        }
    }
}