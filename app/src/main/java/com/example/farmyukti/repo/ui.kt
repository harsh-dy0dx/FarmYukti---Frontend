package com.example.farmyukti.repo


import android.util.Log
import com.example.farmyukti.model.MandiPriceRecord


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


class MandiViewModel : ViewModel() {

    // ⚠️ IMPORTANT: Replace this with your actual API Key from data.gov.in
    private val OGD_API_KEY = "579b464db66ec23bdd000001a96a53bb28f74a446f081aac57df6102"

    // StateFlow to hold the data, making it observable by Compose UI
    private val _mandiRecords = MutableStateFlow<List<MandiPriceRecord>>(emptyList())
    val mandiRecords: StateFlow<List<MandiPriceRecord>> = _mandiRecords

    // MutableStateFlow to handle loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Fetch data immediately when the ViewModel is created
        fetchMandiData()
    }

    fun fetchMandiData(page: Int = 0, state: String? = null) {
        // Start the network operation in a Coroutine
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true

            try {
                val pageSize = 10
                val offsetValue = page * pageSize

                // Execute the Retrofit call
                val response = RetrofitClient.mandiApiService.getMandiPrices(
                    apiKey = OGD_API_KEY,
                    limit = pageSize,
                    offset = offsetValue,
                    stateFilter = state,
                    format = "json"
                )

                // Update the state with the successfully fetched records
                _mandiRecords.value = response.records

            } catch (e: Exception) {
                // Log and handle the error (e.g., show a message to the user)

                println("Network Error: Failed to fetch Mandi data. ${e.message}")
            } finally {
                _isLoading.value = false // Set loading state to false
            }
        }
    }
}













@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiScreen(
    // Get the ViewModel instance (uses default factory)
    viewModel: MandiViewModel = viewModel(),
    navController: NavController
) {
    // 1. Observe the StateFlows
    // Collecting flows as state allows Compose to automatically recompose when the data changes
    val records by viewModel.mandiRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mandi Price Data") }) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {

            // 2. Handle Loading State
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            } else if (records.isEmpty()) {
                // Handle Empty State (No data returned)
                Text(
                    text = "No Mandi price data available.",
                    modifier = Modifier.align(Alignment.Center)
                )

            } else {
                // 3. Display the Data List
                PriceList(records = records)
            }
        }
    }
}
@Composable
fun PriceList(records: List<MandiPriceRecord>) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Iterate over the list of records
        items(records) { record ->
            MandiItemCard(record = record)
        }
    }
}

@Composable
fun MandiItemCard(record: MandiPriceRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Commodity & Market Name
            Text(
                text = "${record.commodity} (${record.variety})",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Text(
                text = "${record.market}, ${record.district}, ${record.state}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Price Details (Modal Price is the most common price)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Arrival Date:", style = MaterialTheme.typography.bodySmall)
                    Text(record.arrivalDate, style = MaterialTheme.typography.bodyLarge)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Modal Price (₹/Quintal):", style = MaterialTheme.typography.bodySmall)
                    // Ensure the price is formatted clearly
                    Text(
                        text = "₹${record.modalPrice}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}