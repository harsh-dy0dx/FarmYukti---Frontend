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
    val featuredCategories = listOf("Rice", "Spices", "Pulses", "Fruits", "Vegetables")
    val recentOrders by appViewModel.listings.collectAsState()
    val userProfile by appViewModel.userProfile.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Welcome Back,", style = MaterialTheme.typography.bodyLarge); Text("Hi ${userProfile?.name ?: "Buyer"}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
                Icon(Icons.Default.AccountCircle, "Profile", modifier = Modifier.size(48.dp).safeClickable{ navController.navigate(Screen.Profile.route) }, tint = MaterialTheme.colorScheme.primary)
            }
        }
        item { OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Search produce...") }, leadingIcon = { Icon(Icons.Default.Search, "Search") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp)) }
        item { Text("Shop by Category", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp)) }
        item { LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(featuredCategories) { CategoryChip(it) } } }
        item { Text("Recent Listings", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) }
        items(recentOrders) { listing -> ProduceListItem(listing = listing, onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) }, modifier = Modifier.padding(horizontal = 16.dp), showChatButton = true) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerMarketplaceScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Marketplace") }, actions = { IconButton(onClick = {}) { Icon(Icons.Default.Search, "Filter") } }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listings) { listing -> ProduceListItem(listing = listing, onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) }, showChatButton = true) }
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