package com.example.farmyukti

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.farmyukti.ui.theme.FarmyuktiTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Composable
fun FarmyuktiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF2E7D32),
            onPrimary = Color.White,
            secondary = Color(0xFF66BB6A),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color(0xFF1B1B1B),
            surface = Color.White,
            onSurface = Color(0xFF1B1B1B),
            primaryContainer = Color(0xFFE8F5E9),
            onPrimaryContainer = Color(0xFF1B5E20)
        ),
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Cloudinary
        // IMPORTANT: Replace with your actual Cloud Name from Cloudinary Dashboard
        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = "YOUR_CLOUD_NAME"
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // MediaManager already initialized
        }

        setContent {
            FarmyuktiTheme {
                FarmYuktiApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Landing : Screen("landing", "Loading", Icons.Default.AccountCircle)
    object Auth : Screen("auth", "Welcome", Icons.Default.AccountCircle)
    object Login : Screen("login", "Login", Icons.Default.AccountCircle)
    object SignUp : Screen("signup", "Sign Up", Icons.Default.AccountCircle)

    object FarmerMain : Screen("farmer_main", "Farmer", Icons.Default.Home)
    object FarmerHome : Screen("farmer_home", "Home", Icons.Default.Home)
    object FarmerListings : Screen("farmer_listings", "Listings", Icons.AutoMirrored.Filled.ListAlt)
    object FarmerAdvisory : Screen("farmer_advisory", "Advisory", Icons.Default.Eco)
    object CreateListing : Screen("create_listing", "New Listing", Icons.Default.Add)

    object FarmerNegotiation : Screen("farmer_negotiation/{listingId}", "Negotiate", Icons.AutoMirrored.Filled.Chat) {
        fun createRoute(listingId: String) = "farmer_negotiation/$listingId"
    }

    object BuyerMain : Screen("buyer_main", "Buyer", Icons.Default.Storefront)
    object BuyerHome : Screen("buyer_home", "Home", Icons.Default.Storefront)
    object BuyerMarketplace : Screen("buyer_marketplace", "Market", Icons.Default.ShoppingCart)
    object BuyerTracking : Screen("buyer_tracking", "Tracking", Icons.Default.LocalShipping)
    object BuyerNegotiation : Screen("buyer_negotiation/{listingId}", "Negotiate", Icons.AutoMirrored.Filled.Chat) {
        fun createRoute(listingId: String) = "buyer_negotiation/$listingId"
    }
}

data class ProduceListing(
    val id: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val contactNumber: String = "",
    val produceName: String = "",
    val quantityKg: String = "",
    val basePricePerKg: String = "",
    val aiQualityGrade: String = "",
    val location: String = "",
    val description: String = "",
    val rating: String = "4.5",
    val imageUrl: String = "" // Empty by default
)

data class Advisory(val id: String, val title: String, val type: AdvisoryType, val summary: String, val date: String)
enum class AdvisoryType { CROP, FERTILIZER, PEST, WEATHER }
data class NegotiationMessage(val id: String, val sender: String, val text: String, val timestamp: Long)
data class UserProfile(val uid: String = "", val role: String = "", val farmerId: String = "")

class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _listings = MutableStateFlow<List<ProduceListing>>(emptyList())
    val listings: StateFlow<List<ProduceListing>> = _listings.asStateFlow()

    init {
        checkCurrentUser()
        fetchListings()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val user = auth.currentUser
            if (user != null) {
                fetchUserRole(user.uid)
            } else {
                _authUiState.value = AuthUiState.SignedOut
                _userRole.value = null
            }
        }
    }

    fun fetchUserRole(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("users").document(uid).get().await()
                val roleString = document.getString("role")
                val role = when (roleString) {
                    "FARMER" -> UserRole.FARMER
                    "BUYER" -> UserRole.BUYER
                    else -> null
                }

                if (role != null) {
                    _userRole.value = role
                    _authUiState.value = AuthUiState.SignedIn
                } else {
                    // Role invalid or missing: Sign out and show error
                    auth.signOut()
                    _userRole.value = null
                    _authUiState.value = AuthUiState.Error("User role not found. Please login again.")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Failed to fetch user role: ${e.message}")
                _userRole.value = null
            }
        }
    }

    fun signUp(email: String, password: String, role: UserRole, farmerId: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val userProfile = mapOf(
                        "uid" to user.uid,
                        "email" to email,
                        "role" to role.name,
                        "farmerId" to if (role == UserRole.FARMER) farmerId else ""
                    )
                    db.collection("users").document(user.uid).set(userProfile).await()

                    auth.signOut()
                    _authUiState.value = AuthUiState.SignUpSuccess
                } else {
                    _authUiState.value = AuthUiState.Error("Sign up failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Sign up failed: ${e.message}")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    fetchUserRole(user.uid)
                } else {
                    _authUiState.value = AuthUiState.Error("Login failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    // --- NEW: Upload Image to Cloudinary & Create Listing ---
    fun createListing(listing: ProduceListing, imageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val user = auth.currentUser

            if (user != null) {
                if (imageUri != null) {
                    // Upload to Cloudinary
                    // IMPORTANT: Replace "your_unsigned_preset" with the preset name from Cloudinary Settings -> Upload
                    MediaManager.get().upload(imageUri)
                        .unsigned("your_unsigned_preset")
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String) { }
                            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) { }

                            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                val imageUrl = resultData["secure_url"] as String
                                saveListingToFirestore(listing, imageUrl, user.uid, onComplete)
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                _authUiState.value = AuthUiState.Error("Image upload failed: ${error.description}")
                            }

                            override fun onReschedule(requestId: String, error: ErrorInfo) { }
                        })
                        .dispatch()
                } else {
                    saveListingToFirestore(listing, "", user.uid, onComplete)
                }
            } else {
                _authUiState.value = AuthUiState.Error("User not logged in")
            }
        }
    }

    private fun saveListingToFirestore(listing: ProduceListing, imageUrl: String, uid: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val newDocRef = db.collection("listings").document()
                val newListing = listing.copy(
                    farmerId = uid,
                    id = newDocRef.id,
                    imageUrl = imageUrl
                )
                newDocRef.set(newListing).await()
                fetchListings()
                _authUiState.value = AuthUiState.Idle
                onComplete()
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Failed to save listing: ${e.message}")
            }
        }
    }

    fun fetchListings() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("listings").get().await()
                val list = snapshot.toObjects(ProduceListing::class.java)
                _listings.value = list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userRole.value = null
        _authUiState.value = AuthUiState.SignedOut
    }

    fun resetAuthState() {
        if (_authUiState.value is AuthUiState.Error || _authUiState.value is AuthUiState.SignUpSuccess) {
            if (auth.currentUser == null) {
                _authUiState.value = AuthUiState.SignedOut
            } else {
                _authUiState.value = AuthUiState.Idle
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object SignedIn : AuthUiState()
    object SignedOut : AuthUiState()
    object SignUpSuccess : AuthUiState()
    data class Error(val message: String?) : AuthUiState()
}

enum class UserRole { FARMER, BUYER }

@Composable
fun FarmYuktiApp(
    appViewModel: AppViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authUiState by appViewModel.authUiState.collectAsState()
    val userRole by appViewModel.userRole.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Error) {
            val message = (authUiState as AuthUiState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            appViewModel.resetAuthState()
        }
    }

    NavHost(navController = navController, startDestination = Screen.Landing.route) {

        composable(Screen.Landing.route) {
            LandingScreen(authUiState, userRole) { destination ->
                navController.navigate(destination) {
                    popUpTo(Screen.Landing.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginClicked = { navController.navigate(Screen.Login.route) },
                onSignUpClicked = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.FarmerMain.route) {
            FarmerMainScreen(
                navController = navController,
                onLogout = {
                    appViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.FarmerMain.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                appViewModel = appViewModel
            )
        }

        composable(Screen.CreateListing.route) {
            CreateListingScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.FarmerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.FARMER)
        }

        composable(Screen.BuyerMain.route) {
            BuyerMainScreen(
                navController = navController,
                onLogout = {
                    appViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.BuyerMain.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                appViewModel = appViewModel
            )
        }
        composable(Screen.BuyerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.BUYER)
        }
    }
}

@Composable
fun LandingScreen(
    authUiState: AuthUiState,
    userRole: UserRole?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()

        LaunchedEffect(authUiState, userRole) {
            when (authUiState) {
                is AuthUiState.SignedIn -> {
                    when (userRole) {
                        UserRole.FARMER -> onNavigate(Screen.FarmerMain.route)
                        UserRole.BUYER -> onNavigate(Screen.BuyerMain.route)
                        null -> {
                            // Should not happen with new fetch logic, but as a fallback:
                            onNavigate(Screen.Auth.route)
                        }
                    }
                }
                is AuthUiState.SignedOut -> {
                    onNavigate(Screen.Auth.route)
                }
                is AuthUiState.Error -> {
                    onNavigate(Screen.Auth.route)
                }
                else -> {
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Eco,
            contentDescription = "FarmYukti Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Welcome to FarmYukti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Direct Market Access for Farmers", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 18.sp)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSignUpClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var farmerId by rememberSaveable { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }

    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.SignUpSuccess) {
            Toast.makeText(context, "Sign up successful! Please log in.", Toast.LENGTH_SHORT).show()
            appViewModel.resetAuthState()
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onClick = { selectedRole = UserRole.FARMER },
                    selected = selectedRole == UserRole.FARMER
                ) {
                    Text("I am a Farmer")
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    onClick = { selectedRole = UserRole.BUYER },
                    selected = selectedRole == UserRole.BUYER
                ) {
                    Text("I am a Buyer")
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min. 6 characters)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Password, contentDescription = "Password") }
            )

            Spacer(Modifier.height(16.dp))

            if (selectedRole == UserRole.FARMER) {
                OutlinedTextField(
                    value = farmerId,
                    onValueChange = { farmerId = it },
                    label = { Text("11-Digit Farmer ID (from AgriStack)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Farmer ID") }
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    appViewModel.signUp(email, password, selectedRole, farmerId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.SignedIn) {
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Landing.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Eco,
                contentDescription = "FarmYukti Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Password, contentDescription = "Password") }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    appViewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Login")
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* TODO: VIO Login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Login", modifier = Modifier.padding(end = 8.dp))
                Text("Login with Voice")
            }
        }
    }
}

@Composable
fun FarmerMainScreen(
    navController: NavController,
    onLogout: () -> Unit,
    appViewModel: AppViewModel = viewModel()
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            FarmerBottomNavigationBar(navController = bottomNavController)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.FarmerHome.route) {
                composable(Screen.FarmerHome.route) {
                    FarmerHomeScreen(navController = navController, onLogout = onLogout)
                }
                composable(Screen.FarmerListings.route) {
                    FarmerListingsScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.FarmerAdvisory.route) {
                    FarmerAdvisoryScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun FarmerBottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.FarmerHome,
        Screen.FarmerListings,
        Screen.FarmerAdvisory
    )
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun FarmerHomeScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    val mockAdvisories = listOf(
        Advisory("w1", "Heavy Rain Warning", AdvisoryType.WEATHER, "Expect heavy rainfall in your region in the next 48 hours. Secure any open storage.", "Nov 1, 2025"),
        Advisory("p1", "Pest Alert: Pod Borer", AdvisoryType.PEST, "Pod Borer activity detected in your area. Immediate action required.", "Nov 1, 2025")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Personalized Early Warnings", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
        }

        items(mockAdvisories) { advisory ->
            WarningAdvisoryCard(advisory = advisory, modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, start = 16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionCard(title = "New Listing", icon = Icons.Filled.AddShoppingCart, onClick = {
                    navController.navigate(Screen.CreateListing.route)
                })

                val context = LocalContext.current
                QuickActionCard(title = "My Chats", icon = Icons.AutoMirrored.Filled.Chat, onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wa.me/")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                })
                QuickActionCard(title = "Pest Scan", icon = Icons.Filled.DocumentScanner, onClick = { /* TODO */ })
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionCard(title = "Market Prices", icon = Icons.Filled.TrendingUp, onClick = { /* TODO */ })
                QuickActionCard(title = "Voice Help", icon = Icons.Default.Mic, onClick = { /* TODO */ })
                QuickActionCard(title = "My Profile", icon = Icons.Filled.AccountCircle, onClick = { /* TODO */ })
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out", modifier = Modifier.padding(end = 8.dp))
                Text("Log Out")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {
    var produceName by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var contactNumber by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var farmerName by rememberSaveable { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var expanded by remember { mutableStateOf(false) }
    val qualityOptions = listOf("Grade A", "Grade B", "Grade C")
    var selectedQuality by remember { mutableStateOf(qualityOptions[0]) }

    val context = LocalContext.current
    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Listing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("Image Selected", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = "Upload", modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Text("Tap to upload Crop Photo", color = Color.Gray)
                    }
                }
            }

            OutlinedTextField(
                value = produceName,
                onValueChange = { produceName = it },
                label = { Text("Crop Name (e.g. Wheat)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = farmerName,
                onValueChange = { farmerName = it },
                label = { Text("Your Name (Display Name)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AccountCircle, "") }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (kg)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per kg (₹)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, "") }
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (City/Village)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.GpsFixed, "") }
            )

            OutlinedTextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("WhatsApp Number (+91...)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, "") }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selectedQuality,
                    onValueChange = {},
                    label = { Text("Quality Grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    qualityOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedQuality = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Additional Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                leadingIcon = { Icon(Icons.Default.Description, "") }
            )

            Button(
                onClick = {
                    if(produceName.isNotEmpty() && price.isNotEmpty()) {
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
                        appViewModel.createListing(newListing, imageUri) {
                            Toast.makeText(context, "Listing Created Successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Listing")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun WarningAdvisoryCard(advisory: Advisory, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerListingsScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Produce Listings") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) },
        floatingActionButton = {
            Button(
                onClick = { navController.navigate(Screen.CreateListing.route) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.padding(end = 8.dp))
                Text("New Listing")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listings) { listing ->
                ProduceListItem(
                    listing = listing,
                    onClick = { /* View Details */ }
                )
            }
        }
    }
}

@Composable
fun ProduceListItem(
    listing: ProduceListing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showChatButton: Boolean = false
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
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
                Text("Qty: ${listing.quantityKg} kg", style = MaterialTheme.typography.bodyMedium)
                Text("Price: ₹${listing.basePricePerKg}/kg", style = MaterialTheme.typography.bodyMedium)
                Text("Loc: ${listing.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    Text(" ${listing.rating}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(listing.aiQualityGrade, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                if(showChatButton) {
                    Spacer(Modifier.height(8.dp))
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=${listing.contactNumber}")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, "Chat", tint = Color(0xFF25D366)) // WhatsApp Green
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerAdvisoryScreen(navController: NavController) {
    val advisoryTypes = listOf("All", "Crop", "Fertilizer", "Pest", "Weather")
    var selectedType by remember { mutableStateOf(advisoryTypes[0]) }

    val mockAdvisories = listOf(
        Advisory("c1", "Crop Rotation Plan", AdvisoryType.CROP, "Consider rotating with legumes to improve soil nitrogen.", "Nov 2, 2025"),
        Advisory("f1", "Fertilizer Dose - NPK", AdvisoryType.FERTILIZER, "Recommended NPK ratio for your soil test is 12:32:16.", "Nov 2, 2025"),
        Advisory("p1", "Pest Alert: Pod Borer", AdvisoryType.PEST, "Pod Borer activity detected in your area. Immediate action required.", "Nov 1, 2025"),
        Advisory("w1", "Heavy Rain Warning", AdvisoryType.WEATHER, "Expect heavy rainfall in your region in the next48 hours.", "Nov 1, 2025")
    )

    val filteredAdvisories = mockAdvisories.filter {
        selectedType == "All" || it.type.name.equals(selectedType, ignoreCase = true)
    }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = "Scan", modifier = Modifier.padding(end = 8.dp))
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
                PestDiagnosisSheetContent(onDismiss = { showPestSheet = false })
            }
        }
    }
}

@Composable
fun AdvisoryCard(advisory: Advisory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (advisory.type) {
                        AdvisoryType.CROP -> Icons.Default.Eco
                        AdvisoryType.FERTILIZER -> Icons.Default.WaterDrop
                        AdvisoryType.PEST -> Icons.Default.PestControl
                        AdvisoryType.WEATHER -> Icons.Default.Warning
                    },
                    contentDescription = advisory.type.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    advisory.type.name,
                    style = MaterialTheme.typography.bodyMedium,
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

@Composable
fun BuyerMainScreen(
    navController: NavController,
    onLogout: () -> Unit,
    appViewModel: AppViewModel // Pass ViewModel to access listings
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BuyerBottomNavigationBar(navController = bottomNavController)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = bottomNavController, startDestination = Screen.BuyerHome.route) {
                composable(Screen.BuyerHome.route) {
                    BuyerHomeScreen(navController = navController, onLogout = onLogout, appViewModel = appViewModel)
                }
                composable(Screen.BuyerMarketplace.route) {
                    BuyerMarketplaceScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.BuyerTracking.route) {
                    BuyerTrackingScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BuyerBottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.BuyerHome,
        Screen.BuyerMarketplace,
        Screen.BuyerTracking
    )
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BuyerHomeScreen(
    navController: NavController,
    onLogout: () -> Unit,
    appViewModel: AppViewModel
) {
    val featuredCategories = listOf("Rice", "Spices", "Pulses", "Fruits", "Vegetables")
    val recentOrders by appViewModel.listings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Welcome, Buyer",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search all produce...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }

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
                    CategoryChip(category)
                }
            }
        }

        item {
            Text(
                "Recent Listings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        items(recentOrders) { listing ->
            ProduceListItem(
                listing = listing,
                onClick = {
                    navController.navigate(Screen.BuyerNegotiation.createRoute(listing.id))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                showChatButton = true // Show chat button for buyers
            )
        }

        item {
            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out", modifier = Modifier.padding(end = 8.dp))
                Text("Log Out")
            }
            Spacer(Modifier.height(16.dp))
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
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerMarketplaceScreen(navController: NavController, appViewModel: AppViewModel) {
    val listings by appViewModel.listings.collectAsState()

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
            items(listings) { listing ->
                ProduceListItem(
                    listing = listing,
                    onClick = {
                        navController.navigate(Screen.BuyerNegotiation.createRoute(listing.id))
                    },
                    showChatButton = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerTrackingScreen(navController: NavController) {
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
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegotiationScreen(
    navController: NavController,
    listingId: String,
    userRole: UserRole
) {
    val mockListing = ProduceListing("l1", "Farmer Ramesh", "Sona Masoori Rice", "500", "45.0", "Grade A", "Guntur, AP")
    var messageText by rememberSaveable { mutableStateOf("") }

    val messages = remember {
        mutableStateOf(listOf(
            NegotiationMessage("m1", "Farmer", "Base price is ₹45/kg", System.currentTimeMillis() - 120000),
            NegotiationMessage("m2", "Buyer", "Willing to take 500kg. What is your best price?", System.currentTimeMillis() - 90000),
            NegotiationMessage("m3", "Buyer", "That's a bit high. Can you do ₹42/kg for the full lot? I can pay immediately.", System.currentTimeMillis() - 60000)
        ))
    }

    val onSend = {
        if (messageText.isNotBlank()) {
            messages.value = messages.value + NegotiationMessage(
                id = (messages.value.size + 1).toString(),
                sender = if (userRole == UserRole.FARMER) "Farmer" else "Buyer",
                text = messageText,
                timestamp = System.currentTimeMillis()
            )
            messageText = ""
        }
    }

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
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showDetailsSheet = false
                            }
                        }
                    },
                    onAccept = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showDetailsSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: NegotiationMessage, myRole: UserRole) {
    val isMine = (myRole == UserRole.FARMER && message.sender == "Farmer") ||
            (myRole == UserRole.BUYER && message.sender == "Buyer")

    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isMine) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
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
            IconButton(onClick = { /* TODO: Voice Input */ }) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.Gray)
            }

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your offer...") },
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(48.dp)
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
    onDismiss: () -> Unit,
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

        DetailRow(icon = Icons.Default.CheckCircle, label = "AI Quality Grade", value = listing.aiQualityGrade)
        DetailRow(icon = Icons.Default.AddShoppingCart, label = "Total Quantity", value = "${listing.quantityKg} kg")
        DetailRow(icon = Icons.Default.Paid, label = "Base Price", value = "₹${listing.basePricePerKg}/kg")
        DetailRow(icon = Icons.Default.GpsFixed, label = "Location", value = listing.location)

        Spacer(Modifier.height(24.dp))

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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}