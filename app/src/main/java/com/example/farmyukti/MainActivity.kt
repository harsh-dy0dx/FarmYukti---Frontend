package com.example.farmyukti // Corrected package to match your project structure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- THEME ---
// In a real app, this would be in its own Theme.kt file
@Composable
fun FarmyuktiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF2E7D32), // Deep Green
            onPrimary = Color.White,
            secondary = Color(0xFF66BB6A), // Lighter, premium green accent
            onSecondary = Color.White,
            background = Color.White, // Pure white background
            onBackground = Color(0xFF1B1B1B), // Dark text
            surface = Color.White, // Pure white surfaces (Cards, etc.)
            onSurface = Color(0xFF1B1B1B), // Dark text on surfaces
            primaryContainer = Color(0xFFE8F5E9), // Very light green for chips/icon backgrounds
            onPrimaryContainer = Color(0xFF1B5E20) // Dark green text/icons on light green
        ),
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

// --- MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmyuktiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FarmYuktiApp()
                }
            }
        }
    }
}

// --- NAVIGATION ---
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object RoleSelection : Screen("role_selection", "Select Role", Icons.Default.AccountCircle)
    object Login : Screen("login", "Login", Icons.Default.AccountCircle)

    // Farmer Screens
    object FarmerHome : Screen("farmer_home", "Home", Icons.Default.Home)
    object FarmerListings : Screen("farmer_listings", "Listings", Icons.AutoMirrored.Filled.ListAlt)
    object FarmerAdvisory : Screen("farmer_advisory", "Advisory", Icons.Default.Eco)
    object FarmerNegotiation : Screen("farmer_negotiation/{listingId}", "Negotiate", Icons.AutoMirrored.Filled.Chat) {
        fun createRoute(listingId: String) = "farmer_negotiation/$listingId"
    }

    // Buyer Screens
    object BuyerHome : Screen("buyer_home", "Home", Icons.Default.Home)
    object BuyerMarketplace : Screen("buyer_marketplace", "Market", Icons.Default.ShoppingCart)
    object BuyerTracking : Screen("buyer_tracking", "Tracking", Icons.Default.LocalShipping)
    object BuyerNegotiation : Screen("buyer_negotiation/{listingId}", "Negotiate", Icons.AutoMirrored.Filled.Chat) {
        fun createRoute(listingId: String) = "buyer_negotiation/$listingId"
    }
}

// --- MOCK DATA MODELS ---
// These models are based on the synopsis
data class ProduceListing(
    val id: String,
    val farmerName: String,
    val produceName: String,
    val quantityKg: Int,
    val basePricePerKg: Double,
    val aiQualityGrade: String, // e.g., "Grade A", "Grade B"
    val location: String,
    val imageUrl: String = "https://placehold.co/600x400/2E7D32/FFFFFF?text=Produce"
)

data class Advisory(
    val id: String,
    val title: String,
    val type: AdvisoryType,
    val summary: String,
    val date: String
)

enum class AdvisoryType { CROP, FERTILIZER, PEST, WEATHER }

data class NegotiationMessage(
    val id: String,
    val sender: String, // "Farmer" or "Buyer"
    val text: String,
    val timestamp: Long
)

// --- VIEWMODEL ---
// A simple ViewModel to manage the app's state, like user role.
// In a real app, you'd have more complex ViewModels with Hilt/Dagger.
class AppViewModel : ViewModel() {
    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole = _userRole.asStateFlow()

    fun selectRole(role: UserRole) {
        _userRole.value = role
    }
}

enum class UserRole { FARMER, BUYER }

// --- MAIN APP COMPOSABLE (Entry Point) ---
@Composable
fun FarmYuktiApp(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = viewModel()
) {
    val userRole by appViewModel.userRole.collectAsState()
    val startDestination = Screen.RoleSelection.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController = navController, onRoleSelected = { role ->
                appViewModel.selectRole(role)
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.RoleSelection.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController, role = userRole ?: UserRole.FARMER)
        }

        // --- Farmer Navigation Graph ---
        composable(Screen.FarmerHome.route) {
            FarmerMainScreen(navController = navController)
        }
        composable(Screen.FarmerListings.route) {
            FarmerMainScreen(navController = navController)
        }
        composable(Screen.FarmerAdvisory.route) {
            FarmerMainScreen(navController = navController)
        }
        composable(Screen.FarmerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.FARMER)
        }

        // --- Buyer Navigation Graph ---
        composable(Screen.BuyerHome.route) {
            BuyerMainScreen(navController = navController)
        }
        composable(Screen.BuyerMarketplace.route) {
            BuyerMainScreen(navController = navController)
        }
        composable(Screen.BuyerTracking.route) {
            BuyerMainScreen(navController = navController)
        }
        composable(Screen.BuyerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.BUYER)
        }
    }
}

// --- ROLE SELECTION SCREEN ---
@Composable
fun RoleSelectionScreen(
    navController: NavController,
    onRoleSelected: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FarmYukti", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Welcome to the Future of Farming", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 48.dp))

        Text("Please select your role:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 24.dp))

        Button(
            onClick = { onRoleSelected(UserRole.FARMER) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Eco, contentDescription = "Farmer", modifier = Modifier.padding(end = 8.dp))
            Text("I am a Farmer", fontSize = 18.sp)
        }

        Button(
            onClick = { onRoleSelected(UserRole.BUYER) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Buyer", modifier = Modifier.padding(end = 8.dp))
            Text("I am a Buyer", fontSize = 18.sp)
        }
    }
}

// --- LOGIN SCREEN ---
@Composable
fun LoginScreen(
    navController: NavController,
    role: UserRole
) {
    var farmerId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isFarmer = role == UserRole.FARMER

    // Mock authentication
    val onLoginClicked = {
        // In a real app, you'd call Firebase Auth here.
        // For now, we just navigate to the correct home screen.
        if (isFarmer) {
            navController.navigate(Screen.FarmerHome.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.BuyerHome.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login as ${if (isFarmer) "Farmer" else "Buyer"}", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        // As per synopsis, farmer login is via 11-digit Farmer ID from AgriStack
        OutlinedTextField(
            value = farmerId,
            onValueChange = { farmerId = it },
            label = { Text(if (isFarmer) "Farmer ID (AgriStack)" else "Buyer ID") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password / OTP") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardType.Password.let { KeyboardOptions(keyboardType = it) },
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(16.dp))

        // As per synopsis, VIO (Voice Input/Output) is critical for accessibility
        OutlinedButton(
            onClick = { /* TODO: Implement Voice Login */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Voice Login", modifier = Modifier.padding(end = 8.dp))
            Text("Login with Voice")
        }
    }
}

// --- FARMER: MAIN SCREEN (with Bottom Nav) ---
@Composable
fun FarmerMainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val screens = listOf(
        Screen.FarmerHome,
        Screen.FarmerListings,
        Screen.FarmerAdvisory
    )

    Scaffold(
        bottomBar = {
            FarmerBottomNavigationBar(navController = bottomNavController, items = screens)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.FarmerHome.route) {
                composable(Screen.FarmerHome.route) {
                    FarmerHomeScreen(navController = navController) // Pass main controller
                }
                composable(Screen.FarmerListings.route) {
                    FarmerListingsScreen(navController = navController) // Pass main controller
                }
                composable(Screen.FarmerAdvisory.route) {
                    FarmerAdvisoryScreen(navController = navController) // Pass main controller
                }
            }
        }
    }
}

@Composable
fun FarmerBottomNavigationBar(navController: NavHostController, items: List<Screen>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// --- FARMER: HOME SCREEN ---
@Composable
fun FarmerHomeScreen(navController: NavController) {
    val mockAdvisories = listOf(
        Advisory("w1", "Heavy Rain Warning", AdvisoryType.WEATHER, "Expect heavy rainfall in your region in the next 48 hours. Secure any open storage.", "Nov 1, 2025"),
        Advisory("p1", "Pest Alert: Aphids", AdvisoryType.PEST, "Aphid populations detected near your plot. Inspect underside of leaves.", "Oct 31, 2025")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Welcome, Farmer!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            // Synopsis mentions personalized early warning system
            Text("Personalized Early Warnings", style = MaterialTheme.typography.titleLarge)
        }

        items(mockAdvisories) { advisory ->
            WarningCard(advisory)
        }

        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionCard(title = "New Listing", icon = Icons.Filled.AddShoppingCart, onClick = { /* TODO */ })
                QuickActionCard(title = "My Chats", icon = Icons.AutoMirrored.Filled.Chat, onClick = { /* TODO */ })
                QuickActionCard(title = "Pest Scan", icon = Icons.Filled.DocumentScanner, onClick = { /* TODO: Open Pest Sheet */ })
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionCard(title = "Market Prices", icon = Icons.Filled.TrendingUp, onClick = { /* TODO */ })
                QuickActionCard(title = "Voice Help", icon = Icons.Default.Mic, onClick = { /* TODO */ })
                QuickActionCard(title = "My Profile", icon = Icons.Filled.AccountCircle, onClick = { /* TODO */ })
            }
        }
    }
}

@Composable
fun WarningCard(advisory: Advisory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (advisory.type == AdvisoryType.WEATHER) Color(0xFFFFF3E0) else Color(0xFFFBE9E7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Warning",
                tint = if (advisory.type == AdvisoryType.WEATHER) Color(0xFFFFA000) else Color(0xFFD32F2F),
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(advisory.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(advisory.summary, style = MaterialTheme.typography.bodyMedium)
                Text(advisory.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp) // Larger, premium feel
                .clip(RoundedCornerShape(16.dp)) // Softer, premium corners
                .background(MaterialTheme.colorScheme.primaryContainer), // Light green background
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary // Main green icon
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

// --- FARMER: LISTINGS SCREEN ---
@OptIn(ExperimentalMaterial3Api::class) // Corrected OptIn
@Composable
fun FarmerListingsScreen(navController: NavController) {
    val mockListings = listOf(
        ProduceListing("l1", "My Farm", "Sona Masoori Rice", 500, 45.0, "Grade A", "Guntur, AP"),
        ProduceListing("l2", "My Farm", "Red Chillies", 200, 220.0, "Grade A", "Guntur, AP")
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Produce Listings") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(onClick = { /* TODO: Add new listing */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("+ Add New Listing")
                }
            }
            items(mockListings) { listing ->
                ProduceListItem(
                    listing = listing,
                    onClick = {
                        navController.navigate(Screen.FarmerNegotiation.createRoute(listing.id))
                    }
                )
            }
        }
    }
}

@Composable
fun ProduceListItem(listing: ProduceListing, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier // Use the modifier passed in
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Placeholder Image
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Replace with Coil
                contentDescription = listing.produceName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(listing.produceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Base Price: ₹${listing.basePricePerKg}/kg", style = MaterialTheme.typography.bodyMedium)
                Text("Quantity: ${listing.quantityKg} kg", style = MaterialTheme.typography.bodyMedium)
                Text("Location: ${listing.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            // AI Quality Grade from synopsis
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(listing.aiQualityGrade, color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// --- FARMER: ADVISORY SCREEN ---
@OptIn(ExperimentalMaterial3Api::class) // Corrected OptIn
@Composable
fun FarmerAdvisoryScreen(navController: NavController) {
    val advisoryTypes = listOf("All", "Crop", "Fertilizer", "Pest", "Weather")
    var selectedType by remember { mutableStateOf(advisoryTypes[0]) }

    val allAdvisories = listOf(
        Advisory("c1", "Crop Rotation Advice", AdvisoryType.CROP, "Consider rotating with legumes to improve soil nitrogen.", "Oct 30, 2025"),
        Advisory("f1", "Fertilizer Timing", AdvisoryType.FERTILIZER, "Apply next round of NPK fertilizer 30 days after sowing.", "Oct 28, 2025"),
        Advisory("p1", "Pest Alert: Aphids", AdvisoryType.PEST, "Aphid populations detected near your plot. Inspect underside of leaves.", "Oct 31, 2025"),
        Advisory("w1", "Heavy Rain Warning", AdvisoryType.WEATHER, "Expect heavy rainfall in your region in the next 48 hours.", "Nov 1, 2025")
    )

    val filteredAdvisories = allAdvisories.filter {
        selectedType == "All" || it.type.name.equals(selectedType, ignoreCase = true)
    }

    // Synopsis requires AI Pest/Disease Management via image upload
    var showPestSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agronomy Advisory") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(
                    onClick = { showPestSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.GpsFixed, contentDescription = "Pest Diagnosis", modifier = Modifier.padding(end = 8.dp))
                    Text("Diagnose Pest/Disease (AI Scan)")
                }
            }

            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    advisoryTypes.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = advisoryTypes.size),
                            onClick = { selectedType = label },
                            selected = label == selectedType
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            items(filteredAdvisories) { advisory ->
                AdvisoryCard(advisory)
            }
        }

        if (showPestSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPestSheet = false },
                sheetState = sheetState
            ) {
                PestDiagnosisSheetContent { showPestSheet = false }
            }
        }
    }
}

@Composable
fun AdvisoryCard(advisory: Advisory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (advisory.type) {
                        AdvisoryType.CROP -> Icons.Default.Eco
                        AdvisoryType.FERTILIZER -> Icons.Default.Star // Placeholder
                        AdvisoryType.PEST -> Icons.Default.Warning
                        AdvisoryType.WEATHER -> Icons.Default.Warning
                    },
                    contentDescription = advisory.type.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    advisory.type.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(advisory.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(advisory.summary, style = MaterialTheme.typography.bodyMedium)
            Text(advisory.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun PestDiagnosisSheetContent(onDismiss: () -> Unit) {
    // This sheet implements the "AI-Powered Pest and Disease Management (PDM)"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AI Pest & Disease Diagnosis", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Upload an image of the affected crop. Our AI will analyze it and provide immediate treatment advice.", textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Launch Camera */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Open Camera")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { /* TODO: Launch Gallery */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Upload from Gallery")
        }
        Spacer(Modifier.height(16.dp))
    }
}


// --- BUYER: MAIN SCREEN (with Bottom Nav) ---
@Composable
fun BuyerMainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val screens = listOf(
        Screen.BuyerHome,
        Screen.BuyerMarketplace,
        Screen.BuyerTracking
    )

    Scaffold(
        bottomBar = {
            BuyerBottomNavigationBar(navController = bottomNavController, items = screens)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.BuyerHome.route) {
                composable(Screen.BuyerHome.route) {
                    BuyerHomeScreen(navController = navController) // Pass main controller
                }
                composable(Screen.BuyerMarketplace.route) {
                    BuyerMarketplaceScreen(navController = navController) // Pass main controller
                }
                composable(Screen.BuyerTracking.route) {
                    BuyerTrackingScreen(navController = navController) // Pass main controller
                }
            }
        }
    }
}

@Composable
fun BuyerBottomNavigationBar(navController: NavHostController, items: List<Screen>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// --- BUYER: HOME SCREEN ---
@Composable
fun BuyerHomeScreen(navController: NavController) {
    // Mock data for Buyer Dashboard
    val featuredCategories = listOf("Rice", "Spices", "Pulses", "Fruits", "Vegetables")
    val recentOrders = listOf(
        ProduceListing("l1", "Farmer Ramesh", "Sona Masoori Rice", 500, 45.0, "Grade A", "Guntur, AP"),
        ProduceListing("l2", "Farmer Suresh", "Red Chillies", 200, 220.0, "Grade A", "Guntur, AP")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Welcome, Buyer!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            // Synopsis: Procurement and Sourcing Dashboard
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Search produce (e.g., 'Sona Masoori')") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        item {
            Text(
                "Featured Categories",
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
                    CategoryChip(category)
                }
            }
        }

        item {
            Text(
                "My Recent Procurements",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp) // <-- FIXED! Changed 'top' to 'vertical' as you suggested.
            )
        }

        items(recentOrders) { listing ->
            ProduceListItem(
                listing = listing,
                onClick = {
                    navController.navigate(Screen.BuyerNegotiation.createRoute(listing.id))
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(category: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer // Will be dark green
        )
    }
}

// --- BUYER: MARKETPLACE SCREEN ---
@OptIn(ExperimentalMaterial3Api::class) // Corrected OptIn
@Composable
fun BuyerMarketplaceScreen(navController: NavController) {
    val mockListings = listOf(
        ProduceListing("l1", "Farmer Ramesh", "Sona Masoori Rice", 500, 45.0, "Grade A", "Guntur, AP"),
        ProduceListing("l2", "Farmer Suresh", "Red Chillies", 200, 220.0, "Grade A", "Guntur, AP"),
        ProduceListing("l3", "Farmer Kiran", "Turmeric", 1000, 180.0, "Grade B", "Erode, TN"),
        ProduceListing("l4", "Farmer Devi", "Alphonso Mango", 300, 150.0, "Grade A", "Ratnagiri, MH")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marketplace") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockListings) { listing ->
                ProduceListItem(
                    listing = listing,
                    onClick = {
                        navController.navigate(Screen.BuyerNegotiation.createRoute(listing.id))
                    }
                )
            }
        }
    }
}

// --- BUYFTER: TRACKING SCREEN ---
@OptIn(ExperimentalMaterial3Api::class) // Corrected OptIn
@Composable
fun BuyerTrackingScreen(navController: NavController) {
    // Synopsis: Procurement Tracking & Real-time Order Tracking (Live GPS)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Procurements") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.LocalShipping, contentDescription = "Tracking", modifier = Modifier.size(80.dp), tint = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Text("No Active Shipments", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
            Text("Your active order tracking will appear here.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            // TODO: In a real app, show a list of active shipments with a map view.
        }
    }
}


// --- SHARED: NEGOTIAITON SCREEN ---
@OptIn(ExperimentalMaterial3Api::class) // Corrected OptIn
@Composable
fun NegotiationScreen(
    navController: NavController,
    listingId: String,
    userRole: UserRole
) {
    val mockListing = ProduceListing("l1", "Farmer Ramesh", "Sona Masoori Rice", 500, 45.0, "Grade A", "Guntur, AP")
    var messageText by rememberSaveable { mutableStateOf("") }

    // Mock messages for the chat
    val messages = remember {
        mutableStateOf(listOf(
            NegotiationMessage("m1", "Buyer", "Hi Ramesh, I'm interested in your Sona Masoori Rice.", System.currentTimeMillis() - 100000),
            NegotiationMessage("m2", "Farmer", "Hello! I can offer the full 500kg. My price is ₹45/kg.", System.currentTimeMillis() - 80000),
            NegotiationMessage("m3", "Buyer", "That's a bit high. Can you do ₹42/kg for the full lot? I can pay immediately.", System.currentTimeMillis() - 60000)
        ))
    }

    val onSend = {
        if(messageText.isNotBlank()) {
            messages.value = messages.value + NegotiationMessage(
                id = (messages.value.size + 1).toString(),
                sender = if (userRole == UserRole.FARMER) "Farmer" else "Buyer",
                text = messageText,
                timestamp = System.currentTimeMillis()
            )
            messageText = ""
        }
    }

    // Synopsis: AI Quality Grading & Secure Escrow Payments
    var showDetailsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mockListing.produceName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        "₹${mockListing.basePricePerKg}/kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(onClick = { showDetailsSheet = true }) {
                        Icon(Icons.Default.Analytics, contentDescription = "Details")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NegotiationInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = onSend
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages.value.reversed()) { message ->
                MessageBubble(message = message, myRole = userRole)
            }
        }

        if (showDetailsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDetailsSheet = false },
                sheetState = sheetState
            ) {
                ListingDetailsSheetContent(
                    listing = mockListing,
                    userRole = userRole,
                    onAccept = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showDetailsSheet = false
                            }
                            // TODO: Navigate to Escrow Payment
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: NegotiationMessage, myRole: UserRole) {
    val isMine = (myRole == UserRole.FARMER && message.sender == "Farmer") || // Corrected: UserRole.FARMER
            (myRole == UserRole.BUYER && message.sender == "Buyer")

    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (isMine) Color.White else Color.Black
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 48.dp else 0.dp,
                end = if (isMine) 0.dp else 48.dp
            ),
        contentAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun NegotiationInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // VIO Button
            IconButton(onClick = { /* TODO: Voice Input */ }) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Input")
            }

            OutlinedTextField( // CHANGED from TextField to OutlinedTextField
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your offer...") },
                shape = RoundedCornerShape(24.dp) // ADDED shape back
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun ListingDetailsSheetContent(
    listing: ProduceListing,
    userRole: UserRole,
    onAccept: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Deal Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text(listing.produceName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // AI Quality Grade
        DetailRow(icon = Icons.Default.CheckCircle, label = "AI Quality Grade", value = listing.aiQualityGrade)
        DetailRow(icon = Icons.Default.ShoppingCart, label = "Quantity", value = "${listing.quantityKg} kg")
        DetailRow(icon = Icons.Default.Paid, label = "Base Price", value = "₹${listing.basePricePerKg}/kg")
        DetailRow(icon = Icons.Default.GpsFixed, label = "Location", value = listing.location)

        Spacer(Modifier.height(24.dp))

        // Final action button
        Button(
            onClick = onAccept,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Paid, contentDescription = "", modifier = Modifier.padding(end = 8.dp))
            Text(if (userRole == UserRole.BUYER) "Accept & Move to Escrow" else "Accept Final Offer")
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, // Explicitly named parameter
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary // ADDED tint back
        )
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}



