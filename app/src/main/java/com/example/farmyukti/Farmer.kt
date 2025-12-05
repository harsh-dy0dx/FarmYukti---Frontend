package com.example.farmyukti

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

@Composable
fun FarmerMainScreen(navController: NavController, appViewModel: AppViewModel) {
    val bottomNavController = rememberNavController()
    Scaffold(bottomBar = {
        NavigationBar {
            val items = listOf(Screen.FarmerHome, Screen.FarmerListings, Screen.FarmerAdvisary, Screen.Profile)
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
            NavHost(navController = bottomNavController, startDestination = Screen.FarmerHome.route) {
                composable(Screen.FarmerHome.route) { FarmerHomeScreen(navController, appViewModel) }
                composable(Screen.FarmerListings.route) { FarmerListingsScreen(navController, appViewModel) }
                composable(Screen.Profile.route) { ProfileScreen(navController, appViewModel) }
                composable(Screen.FarmerAdvisary.route) { FarmerAdvisoryScreen(navController) }
            }
        }
    }
}

@Composable
fun FarmerHomeScreen(navController: NavController, appViewModel: AppViewModel) {
    val userProfile by appViewModel.userProfile.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("Welcome Back,", style = MaterialTheme.typography.bodyLarge); Text("Hi ${userProfile?.name ?: "Farmer"}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
                Icon(Icons.Default.AccountCircle, "Profile", modifier = Modifier.size(48.dp).clickable { navController.navigate(Screen.Profile.route) }, tint = MaterialTheme.colorScheme.primary)
            }
        }
        item { AutoSlidingBanner(DataModelist) { } }
        item { Text("Quick Actions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, start = 16.dp)) }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                QuickActionCard("New Listing", Icons.Filled.AddShoppingCart) { navController.navigate(Screen.CreateListing.route) }
                QuickActionCard("Crop Rec", Icons.AutoMirrored.Filled.Chat) { navController.navigate(Screen.CropRec.route) }
                QuickActionCard("Pest Scan", Icons.Filled.DocumentScanner) { navController.navigate(Screen.PestControl.route) }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                QuickActionCard("Market Prices", Icons.Filled.TrendingUp) { navController.navigate(Screen.Mandi.route) }
                QuickActionCard("Favourites", Icons.Default.Favorite) { navController.navigate(Screen.Favorites.route) }
                QuickActionCard("Profile", Icons.Filled.AccountCircle) { navController.navigate(Screen.Profile.route) }
            }
        }
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

// --- UPDATED FARMER LISTINGS SCREEN WITH TABS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerListingsScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()
    val userProfile by appViewModel.userProfile.collectAsState()
    val currentUserId = userProfile?.uid ?: ""

    // 0 = My Produce, 1 = Market Listings
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("My Produce", "Market Listings")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Produce Listings") })
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Only show "New Listing" button if on "My Produce" tab (Optional UX choice, kept generic here)
            Button(onClick = { navController.navigate(Screen.CreateListing.route) }, shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Default.Add, "Add", modifier = Modifier.padding(end = 8.dp))
                Text("New Listing")
            }
        }
    ) { padding ->

        // Filter Logic
        val filteredListings = if (selectedTabIndex == 0) {
            // My Produce: Show only my listings
            listings.filter { it.farmerId == currentUserId }
        } else {
            // Market Listings: Show everything EXCEPT my listings (or show all if you prefer)
            listings.filter { it.farmerId != currentUserId }
        }

        if (filteredListings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTabIndex == 0) "You haven't added any produce yet." else "No market listings available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredListings) { listing ->
                    ProduceListItem(
                        listing = listing,
                        onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) },
                        // Only allow deletion if we are in "My Produce" tab
                        onDelete = if (selectedTabIndex == 0) {
                            { appViewModel.deleteListing(listing.id) }
                        } else {
                            // Pass empty lambda or logic to hide delete button in your ProduceListItem
                            {}
                        }
                    )
                }
            }
        }
    }
}