package com.example.farmyukti

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.farmyukti.repo.safeClickable

@Composable
fun BuyerMainScreen(navController: NavController, appViewModel: AppViewModel) {
    val bottomNavController = rememberNavController()
    Scaffold(bottomBar = {
        NavigationBar {
            val items = listOf(Screen.BuyerHome, Screen.BuyerMarketplace, Screen.BuyerTracking, Screen.Profile)
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { screen ->
                NavigationBarItem(icon = { Icon(screen.icon, screen.label) }, label = { Text(screen.label) }, selected = currentRoute == screen.route, onClick = {
                    bottomNavController.navigate(screen.route) { popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true }
                })
            }
        }
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.BuyerHome.route) {
                composable(Screen.BuyerHome.route) { BuyerHomeScreen(navController, appViewModel) }
                composable(Screen.BuyerMarketplace.route) { BuyerMarketplaceScreen(navController, appViewModel) }
                composable(Screen.BuyerTracking.route) { BuyerTrackingScreen(navController) }
                composable(Screen.Profile.route) { ProfileScreen(navController, appViewModel) }
            }
        }
    }
}

@Composable
fun BuyerHomeScreen(navController: NavController, appViewModel: AppViewModel) {
    val featuredCategories = listOf("All", "Rice", "Spices", "Pulses", "Fruits", "Vegetables")

    // 1. Get Data
    val allListings by appViewModel.listings.collectAsState()
    val userProfile by appViewModel.userProfile.collectAsState()

    // 2. Force Refresh Data when screen opens (Fixes empty list issue)
    LaunchedEffect(Unit) {
        appViewModel.fetchListings()
    }

    // 3. Local Filter State
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // 4. Smart Filtering Logic
    val filteredListings = remember(allListings, searchQuery, selectedCategory) {
        allListings.filter { listing ->
            // Filter by Search (Name or Farmer)
            val matchesSearch = if (searchQuery.isBlank()) true else {
                listing.produceName.contains(searchQuery, ignoreCase = true) ||
                        listing.farmerName.contains(searchQuery, ignoreCase = true)
            }

            // Filter by Category (Contains, not Equals)
            val matchesCategory = if (selectedCategory == "All") true else {
                listing.produceName.contains(selectedCategory, ignoreCase = true)
            }

            matchesSearch && matchesCategory
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Welcome Back,", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Hi ${userProfile?.name ?: "Buyer"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.AccountCircle,
                    "Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .safeClickable { navController.navigate(Screen.Profile.route) },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search produce...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // Category Chips
        item {
            Text(
                "Shop by Category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredCategories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Listings List
        item {
            Text(
                "Recent Listings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        if (filteredListings.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No items found.", color = Color.Gray)
                }
            }
        } else {
            items(filteredListings) { listing ->
                ProduceListItem(
                    listing = listing,
                    onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    showChatButton = true
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerMarketplaceScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()

    // Force Refresh
    LaunchedEffect(Unit) {
        appViewModel.fetchListings()
    }

    // --- Local Filter State ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedGrade by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }

    // --- Derived Lists ---
    val availableCategories = remember(listings) { appViewModel.getUniqueCategories(listings) }
    val availableLocations = remember(listings) { appViewModel.getUniqueLocations(listings) }
    val availableGrades = listOf("All", "Grade A", "Grade B", "Grade C")

    // Use the updated filterListings from ViewModel
    val filteredListings = remember(listings, searchQuery, selectedCategory, selectedGrade, selectedLocation) {
        appViewModel.filterListings(
            originalList = listings,
            query = searchQuery,
            category = if (selectedCategory == "All") null else selectedCategory,
            grade = if (selectedGrade == "All") null else selectedGrade,
            location = if (selectedLocation == "All") null else selectedLocation
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Marketplace") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            SearchAndFilterSection(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                categories = availableCategories,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                grades = availableGrades,
                selectedGrade = selectedGrade,
                onGradeChange = { selectedGrade = it },
                locations = availableLocations,
                selectedLocation = selectedLocation,
                onLocationChange = { selectedLocation = it }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredListings.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No crops match your filters.", color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredListings) { listing ->
                        ProduceListItem(
                            listing = listing,
                            onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) },
                            showChatButton = true
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerTrackingScreen(navController: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Track Procurements") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.LocalShipping, "Tracking", modifier = Modifier.size(80.dp), tint = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Text("No Active Shipments", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
            Text("Your active order tracking will appear here.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    }
}