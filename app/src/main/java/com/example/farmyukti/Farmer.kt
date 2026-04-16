package com.example.farmyukti

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.farmyukti.repo.safeClickable
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import coil.request.ImageRequest
import com.example.farmyukti.repo.MandiScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavGraph.Companion.findStartDestination


// --- UI Models ---
data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val gradientColors: List<Color>
)

data class Category(val name: String, val emoji: String, val color: Color)





@Composable
fun FarmerMainScreen(navController: NavController, appViewModel: AppViewModel) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val items = listOf(
                    Triple(Screen.FarmerHome.route, "Home", Icons.Default.Home),
                    Triple(Screen.FarmerListings.route, "Market", Icons.Default.LocalMall),
                    Triple(Screen.FarmerAdvisary.route, "Ask AI", Icons.Default.ChatBubble),
                    Triple(Screen.Mandi.route, "MSP", Icons.Default.TrendingUp),
                    Triple(Screen.Profile.route, "Profile", Icons.Default.Person)
                )

                items.forEach { (route, label, icon) ->
                    val isSelected = currentRoute == route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(route) {
                                // Pops up to Home to avoid stack buildup
                                popUpTo(Screen.FarmerHome.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, null, tint = if (isSelected) Color(0xFF4A7C59) else Color.Gray) },
                        label = { Text(label, color = if (isSelected) Color(0xFF4A7C59) else Color.Gray) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.FarmerHome.route) {
                // Pass BOTH controllers to the Home Screen
                composable(Screen.FarmerHome.route) {
                    FarmerHomeScreen(
                        parentNavController = navController,
                        innerNavController = bottomNavController,
                        appViewModel = appViewModel
                    )
                }

                composable(Screen.FarmerListings.route) {
                    FarmerListingsScreen(navController = bottomNavController, appViewModel = appViewModel)
                }

                composable(Screen.FarmerAdvisary.route) { AIChatbotScreen(navController = navController) }

                composable(Screen.Mandi.route) {
                    MandiScreen(navController = bottomNavController)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(navController = bottomNavController, appViewModel = appViewModel)
                }

                // Keep these inside the inner NavHost so the Bottom Bar stays visible
                composable("weather") { WeatherScreen(appViewModel) }
                composable("learn") { LearningResourcesScreen() }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, appViewModel: AppViewModel) {
    val userProfile by appViewModel.userProfile.collectAsState()
    val verificationStatus by appViewModel.verificationStatus.collectAsState()

    val isVerified = userProfile?.isVerified == true || verificationStatus is VerificationState.Success

    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var mobile by remember { mutableStateOf(userProfile?.mobile ?: "") }
    var agriStackId by remember { mutableStateOf(userProfile?.agriStackId ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) appViewModel.uploadProfileImage(context, uri) }
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFF4A7C59), Color(0xFF059669))),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                ).padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 80.dp)
            ) {
                Text("Profile", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-60).dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(modifier = Modifier.size(80.dp).safeClickable { singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, color = Color(0xFF4A7C59).copy(alpha = 0.1f), shape = CircleShape) {
                            if (userProfile?.photoUrl.isNullOrEmpty()) Box(contentAlignment = Alignment.Center) { Text("👨‍🌾", fontSize = 40.sp) }
                            else AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(userProfile?.photoUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            if (isEditing) OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Edit Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            else Text(userProfile?.name ?: "Farmer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                            Text(userProfile?.role ?: "Farmer", color = Color.Gray)

                            Surface(color = if (isVerified) Color(0xFFDCFCE7) else Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                Text(text = if (isVerified) "Verified Account" else "Account Not Verified", color = if (isVerified) Color(0xFF15803D) else Color(0xFFDC2626), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ProfileStatCard("Listings", "12", Icons.Default.Inventory2, Color(0xFF2563EB), Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        ProfileStatCard("Sales", "₹45k", Icons.Default.TrendingUp, Color(0xFF16A34A), Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        ProfileStatCard("Rating", "4.8", Icons.Default.Star, Color(0xFFB45309), Modifier.weight(1f))
                    }

                    if (isEditing) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Edit Mobile") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { appViewModel.updateUserProfile(name, mobile, agriStackId) { isEditing = false; Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show() } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7C59))) { Text("Save Changes") }
                    } else {
                        Spacer(Modifier.height(24.dp))
                        ProfileInfoRow(Icons.Default.Email, "Email", userProfile?.email ?: "")
                        ProfileInfoRow(Icons.Default.Phone, "Phone", userProfile?.mobile ?: "Not linked")
                    }
                }
            }
        }

        if (userProfile?.role == "FARMER" && !isVerified) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-40).dp), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Farmer Verification", fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = agriStackId, onValueChange = { agriStackId = it }, label = { Text("AgriStack ID") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))
                        AgriStackVerificationButton(agriStackId, verificationStatus) { appViewModel.verifyAgriStackId(agriStackId) }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = if (isVerified) (-40).dp else (-20).dp), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Settings", fontWeight = FontWeight.Bold)
                    SettingsRow(Icons.Default.PersonOutline, "Edit Profile/Name") { isEditing = !isEditing }
                    SettingsRow(Icons.AutoMirrored.Filled.Logout, "Logout", Color.Red) {
                        appViewModel.logout()
                        navController.navigate(Screen.Auth.route) { popUpTo(0) }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(modifier = Modifier.size(40.dp), color = Color(0xFFF3F4F6), shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) } }
        Column { Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray); Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium) }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, label: String, color: Color = Color.Black, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).safeClickable { onClick() }, color = Color.Transparent) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(icon, null, tint = if (color == Color.Red) color else Color.Gray)
            Text(label, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AgriStackVerificationButton(
    agriStackId: String,
    verificationStatus: VerificationState,
    onVerifyClick: () -> Unit
) {
    val isSuccess = verificationStatus is VerificationState.Success
    val isError = verificationStatus is VerificationState.Error
    val isLoading = verificationStatus is VerificationState.Loading

    val buttonColor by animateColorAsState(
        targetValue = when {
            isSuccess -> Color(0xFF16A34A)
            isError -> Color(0xFFDC2626)
            else -> Color(0xFF059669)
        },
        label = "ButtonColor"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onVerifyClick,
            enabled = !isLoading && !isSuccess,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = if (isSuccess) Color(0xFF16A34A) else Color.Gray
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            AnimatedContent(targetState = verificationStatus, label = "ButtonContent") { state ->
                when (state) {
                    is VerificationState.Loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    is VerificationState.Success -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Verified", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> Text("Verify AgriStack ID", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (verificationStatus is VerificationState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = verificationStatus.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// --- 2. UPGRADED FARMER LISTINGS SCREEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerListingsScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()
    val userProfile by appViewModel.userProfile.collectAsState()
    val currentUserId = userProfile?.uid ?: ""

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var selectedGrade by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }

    val baseList = remember(listings, selectedTabIndex, currentUserId) {
        if (selectedTabIndex == 1) listings.filter { it.farmerId == currentUserId }
        else listings.filter { it.farmerId != currentUserId }
    }

    val availableCategories = remember(baseList) { appViewModel.getUniqueCategories(baseList) }
    val availableLocations = remember(baseList) { appViewModel.getUniqueLocations(baseList) }
    val availableGrades = listOf("All", "Grade A", "Grade B", "Grade C")

    val finalFilteredList = remember(baseList, appViewModel.sharedSearchQuery, appViewModel.sharedSelectedCategory, selectedGrade, selectedLocation) {
        appViewModel.filterListings(
            originalList = baseList,
            query = appViewModel.sharedSearchQuery,
            category = if (appViewModel.sharedSelectedCategory == "All") null else appViewModel.sharedSelectedCategory,
            grade = if (selectedGrade == "All") null else selectedGrade,
            location = if (selectedLocation == "All") null else selectedLocation
        )
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateListing.route) },
                    containerColor = Color(0xFF4A7C59),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFBF7))
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4A7C59), Color(0xFF059669))
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Text(
                        text = "Produce Marketplace",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = appViewModel.sharedSearchQuery,
                        onValueChange = { appViewModel.sharedSearchQuery = it },
                        placeholder = { Text("Search produce...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { selectedTabIndex = 0 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTabIndex == 0) Color.White else Color.White.copy(alpha = 0.2f),
                                contentColor = if (selectedTabIndex == 0) Color(0xFF4A7C59) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Market", fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { selectedTabIndex = 1 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTabIndex == 1) Color.White else Color.White.copy(alpha = 0.2f),
                                contentColor = if (selectedTabIndex == 1) Color(0xFF4A7C59) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("My Produce", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterDropdown(
                        label = "Category",
                        options = availableCategories,
                        selected = appViewModel.sharedSelectedCategory,
                        onSelect = { appViewModel.sharedSelectedCategory = it }
                    )
                }
                item {
                    FilterDropdown(label = "Grade", options = availableGrades, selected = selectedGrade, onSelect = { selectedGrade = it })
                }
                item {
                    FilterDropdown(label = "Location", options = availableLocations, selected = selectedLocation, onSelect = { selectedLocation = it })
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                if (finalFilteredList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (appViewModel.sharedSearchQuery.isNotEmpty()) "No matches found" else "No listings available",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    items(finalFilteredList) { listing ->
                        ProduceListItem(
                            listing = listing,
                            onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) },
                            showChatButton = (selectedTabIndex == 0),
                            onDelete = if (selectedTabIndex == 1) { { appViewModel.deleteListing(listing.id) } } else null
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
@Composable
fun FarmerHomeScreen(
    parentNavController: NavController, // For Create Listing, Pest Scan
    innerNavController: NavController,  // For Market, Weather, Learn
    appViewModel: AppViewModel
) {
    val userProfile by appViewModel.userProfile.collectAsState()

    val categories = listOf(
        Category("Rice", "🌾", Color(0xFFFEF3C7)),
        Category("Spices", "🌶️", Color(0xFFFEE2E2)),
        Category("Pulses", "🫘", Color(0xFFFEF9C3)),
        Category("Fruits", "🍎", Color(0xFFFCE7F3)),
        Category("Vegetables", "🥬", Color(0xFFD1FAE5)),
        Category("Grains", "🌽", Color(0xFFFFEDD5)),
    )

    // Routing Logic: Full-screen vs. Tab-screen
    val quickActions = listOf(
        QuickAction("Create Listings", "Sell your crop", Icons.Default.AddShoppingCart, Screen.CreateListing.route, listOf(Color(0xFF9333EA), Color(0xFF6366F1))),
        QuickAction("Pest Scan", "Diagnose diseases", Icons.Default.BugReport, Screen.PestControl.route, listOf(Color(0xFF10B981), Color(0xFF0D9488))),
        QuickAction("MSP Prices", "Market rates today", Icons.AutoMirrored.Filled.TrendingUp, Screen.Mandi.route, listOf(Color(0xFFF97316), Color(0xFFF59E0B))),
        QuickAction("Crop Advisor", "Soil-based tips", Icons.Default.Eco, Screen.CropRec.route, listOf(Color(0xFF22C55E), Color(0xFF10B981))),
        QuickAction("Weather", "Daily forecasts", Icons.Default.Cloud, "weather", listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))),
        QuickAction("Guided Learnings", "Watch tutorials", Icons.Default.PlayCircle, "learn", listOf(Color(0xFFEC4899), Color(0xFFF43F5E)))
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFFDFBF7))) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFF4A7C59), Color(0xFF059669))),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                ).padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Welcome Back,", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                            Text("Hi ${userProfile?.name ?: "Farmer"}", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = appViewModel.sharedSearchQuery,
                        onValueChange = { appViewModel.sharedSearchQuery = it },
                        placeholder = { Text("Search crops in marketplace...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            appViewModel.sharedSelectedCategory = "All"
                            innerNavController.navigate(Screen.FarmerListings.route)
                        }),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Shop by Category", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(horizontal = 24.dp).height(240.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).safeClickable {
                            appViewModel.sharedSelectedCategory = category.name
                            innerNavController.navigate(Screen.FarmerListings.route)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = category.color)
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(text = category.emoji, fontSize = 32.sp)
                            Text(text = category.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 24.dp).height(550.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(quickActions) { action ->
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).safeClickable {
                            // LOGIC: If it's a "Full-Screen" route, use Parent. Otherwise, use Inner.
                            val fullScreenRoutes = listOf(
                                Screen.CreateListing.route,
                                Screen.PestControl.route,
                                Screen.CropRec.route
                            )

                            if (action.route in fullScreenRoutes) {
                                parentNavController.navigate(action.route)
                            } else {
                                innerNavController.navigate(action.route)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(action.gradientColors)).padding(20.dp)) {
                            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                Icon(imageVector = action.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                Column {
                                    Text(text = action.title, color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = action.subtitle, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(navController: NavController, appViewModel: AppViewModel) {
    val context = LocalContext.current

    // --- State Variables ---
    var produceName by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var contactNumber by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var farmerName by rememberSaveable { mutableStateOf("") }

    // CHANGED: Now using a list of URIs instead of a single Uri
    val imageUris = remember { mutableStateListOf<Uri>() }

    var expanded by remember { mutableStateOf(false) }
    val qualityOptions = listOf("Grade A", "Grade B", "Grade C")
    var selectedQuality by remember { mutableStateOf(qualityOptions[0]) }

    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading

    // --- Launchers ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        uris.forEach { uri ->
            if (imageUris.size < 3 && !imageUris.contains(uri)) {
                imageUris.add(uri)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null && imageUris.size < 3) {
            val uri = writeBitmapToTempFile(context, bitmap)
            imageUris.add(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Listing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Image Selection Section ---
            Text("Crop Photos (Max 3)", style = MaterialTheme.typography.labelLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    enabled = imageUris.size < 3,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }

                OutlinedButton(
                    onClick = {
                        val remaining = 3 - imageUris.size
                        if (remaining > 0) {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    },
                    enabled = imageUris.size < 3,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
            }

            if (imageUris.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    imageUris.forEachIndexed { index, uri ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageUris.removeAt(index) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(0.6f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No images selected", color = Color.Gray)
                }
            }

            // --- Form Fields ---
            OutlinedTextField(value = produceName, onValueChange = { produceName = it }, label = { Text("Crop Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = farmerName, onValueChange = { farmerName = it }, label = { Text("Your Name") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price/kg") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactNumber, onValueChange = { contactNumber = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(modifier = Modifier.menuAnchor().fillMaxWidth(), readOnly = true, value = selectedQuality, onValueChange = {}, label = { Text("Quality Grade") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    qualityOptions.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { selectedQuality = option; expanded = false }) }
                }
            }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Button(
                onClick = {
                    if (produceName.isNotEmpty() && price.isNotEmpty()) {
                        val newListing = ProduceListing(
                            produceName = produceName,
                            farmerName = farmerName,
                            quantityKg = quantity,
                            basePricePerKg = price,
                            location = location,
                            contactNumber = contactNumber,
                            aiQualityGrade = selectedQuality,
                            description = description
                        )
                        appViewModel.createListing(newListing, imageUris) {
                            Toast.makeText(context, "Listing Created!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) else Text("Create Listing")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun writeBitmapToTempFile(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
    file.createNewFile()
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.close()
    return Uri.fromFile(file)
}