package com.example.farmyukti

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.TextButton
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
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.farmyukti.repo.MandiScreen
//import com.example.farmyukti.ui.MandiScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.UUID

// --- THEME ---
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

        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = "dhrqr1wiv" // Your Cloud Name
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // MediaManager already initialized
        }

        setContent {
            FarmyuktiTheme {
                FarmYuktiApp()
                //PlantDiagnosisScreen()
                //CropRecommendationScreen()


//                AutoSlidingBanner(DataModelist,onBannerClick = { clickedBanner ->
//                    println("Clicked on: ${clickedBanner.title}")
//                })
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

    object ListingDetail : Screen("listing_detail/{listingId}", "Detail", Icons.Default.Description) {
        fun createRoute(listingId: String) = "listing_detail/$listingId"
    }

    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)


    object Mandi : Screen("mandi", "Mandi Prices", Icons.Default.TrendingUp)
    object FarmerAdvisary : Screen("FarmerAdi", "Advisery", Icons.Default.Eco)
    object PestControl : Screen("pest_control", "Pest Control", Icons.Default.PestControl)
    object CropRec : Screen("crop_rec", "Crop Recommendation", Icons.Default.WaterDrop)
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
    val imageUrl: String = ""
)

data class Advisory(val id: String, val title: String, val type: AdvisoryType, val summary: String, val date: String)
enum class AdvisoryType { CROP, FERTILIZER, PEST, WEATHER }
data class NegotiationMessage(val id: String, val sender: String, val text: String, val timestamp: Long)
data class UserProfile(
    val uid: String = "",
    val role: String = "",
    val email: String = "",
    val name: String = "",
    val mobile: String = "",
    val agriStackId: String = "",
    val isVerified: Boolean = false,
    val favorites: List<String> = emptyList()
)

class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _verificationStatus = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationStatus: StateFlow<VerificationState> = _verificationStatus.asStateFlow()

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _listings = MutableStateFlow<List<ProduceListing>>(emptyList())
    val listings: StateFlow<List<ProduceListing>> = _listings.asStateFlow()

    // State for filtered favorites
    private val _favoriteListings = MutableStateFlow<List<ProduceListing>>(emptyList())
    val favoriteListings: StateFlow<List<ProduceListing>> = _favoriteListings.asStateFlow()

    init {
        checkCurrentUser()
        fetchListings()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val user = auth.currentUser
            if (user != null) {
                fetchUserData(user.uid)
            } else {
                _authUiState.value = AuthUiState.SignedOut
                _userRole.value = null
                _userProfile.value = null
            }
        }
    }

    fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("users").document(uid).get().await()
                val profile = document.toObject(UserProfile::class.java)

                if (profile != null) {
                    _userProfile.value = profile
                    _userRole.value = when (profile.role) {
                        "FARMER" -> UserRole.FARMER
                        "BUYER" -> UserRole.BUYER
                        else -> null
                    }
                    _authUiState.value = AuthUiState.SignedIn
                    updateFavoriteListings()
                } else {
                    auth.signOut()
                    _userRole.value = null
                    _authUiState.value = AuthUiState.Error("User profile not found. Please login again.")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Failed to fetch user data: ${e.message}")
                _userRole.value = null
            }
        }
    }

    private fun updateFavoriteListings() {
        val allListings = _listings.value
        val favIds = _userProfile.value?.favorites ?: emptyList()
        _favoriteListings.value = allListings.filter { favIds.contains(it.id) }
    }

    fun updateUserProfile(name: String, mobile: String, agriStackId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                val updates = mapOf(
                    "name" to name,
                    "mobile" to mobile,
                    "agriStackId" to agriStackId
                )
                try {
                    db.collection("users").document(user.uid).update(updates).await()
                    fetchUserData(user.uid)
                    onComplete()
                } catch (e: Exception) {
                    _authUiState.value = AuthUiState.Error("Failed to update profile: ${e.message}")
                }
            }
        }
    }

    fun verifyAgriStackId(agriStackId: String) {
        viewModelScope.launch {
            if (agriStackId.length != 11) {
                _verificationStatus.value = VerificationState.Error("ID must be exactly 11 digits")
                return@launch
            }

            _verificationStatus.value = VerificationState.Loading

            try {
                val agriRef = db.collection("agristack_ids").document(agriStackId)
                val agriDoc = agriRef.get().await()

                if (agriDoc.exists()) {
                    val linkedEmail = agriDoc.getString("linked_email")
                    val currentUserEmail = auth.currentUser?.email

                    if (!linkedEmail.isNullOrEmpty() && linkedEmail != currentUserEmail) {
                        _verificationStatus.value = VerificationState.Error("This ID is already linked to another account ($linkedEmail).")
                        return@launch
                    }

                    if (linkedEmail.isNullOrEmpty() && currentUserEmail != null) {
                        agriRef.update("linked_email", currentUserEmail).await()
                    }

                    val user = auth.currentUser
                    if (user != null) {
                        db.collection("users").document(user.uid).update("isVerified", true).await()
                        fetchUserData(user.uid)
                        _verificationStatus.value = VerificationState.Success
                    }
                } else {
                    _verificationStatus.value = VerificationState.Error("Invalid AgriStack ID. Not found in database.")
                }

            } catch (e: Exception) {
                _verificationStatus.value = VerificationState.Error("Verification failed: ${e.message}")
            }
        }
    }

    fun resetVerificationStatus() {
        _verificationStatus.value = VerificationState.Idle
    }

    fun toggleFavorite(listingId: String) {
        val user = auth.currentUser ?: return
        val currentFavorites = _userProfile.value?.favorites ?: emptyList()

        val isFavorite = currentFavorites.contains(listingId)
        val newFavorites = if (isFavorite) {
            currentFavorites - listingId
        } else {
            currentFavorites + listingId
        }

        viewModelScope.launch {
            try {
                db.collection("users").document(user.uid).update("favorites", newFavorites).await()
                fetchUserData(user.uid)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun signUp(email: String, password: String, role: UserRole, farmerId: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                if (role == UserRole.FARMER) {
                    val agriRef = db.collection("agristack_ids").document(farmerId)
                    val agriDoc = agriRef.get().await()

                    if (!agriDoc.exists()) {
                        _authUiState.value = AuthUiState.Error("Invalid AgriStack ID. ID not found in our records.")
                        return@launch
                    }

                    val linkedEmail = agriDoc.getString("linked_email")
                    if (!linkedEmail.isNullOrEmpty()) {
                        _authUiState.value = AuthUiState.Error("This AgriStack ID is already claimed by another email.")
                        return@launch
                    }

                    val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                    val user = authResult.user

                    if (user != null) {
                        agriRef.update("linked_email", email).await()

                        user.sendEmailVerification().await()

                        val userProfile = UserProfile(
                            uid = user.uid,
                            email = email,
                            role = role.name,
                            agriStackId = farmerId,
                            isVerified = true
                        )
                        db.collection("users").document(user.uid).set(userProfile).await()

                        auth.signOut()
                        _authUiState.value = AuthUiState.SignUpSuccess
                    } else {
                        _authUiState.value = AuthUiState.Error("Sign up failed: User creation failed.")
                    }
                } else {
                    val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                    val user = authResult.user
                    if (user != null) {
                        user.sendEmailVerification().await()

                        val userProfile = UserProfile(
                            uid = user.uid,
                            email = email,
                            role = role.name,
                            agriStackId = "",
                            isVerified = false
                        )
                        db.collection("users").document(user.uid).set(userProfile).await()

                        auth.signOut()
                        _authUiState.value = AuthUiState.SignUpSuccess
                    } else {
                        _authUiState.value = AuthUiState.Error("Sign up failed: User creation failed.")
                    }
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
                    if (user.isEmailVerified) {
                        fetchUserData(user.uid)
                    } else {
                        auth.signOut()
                        _authUiState.value = AuthUiState.Error("Please verify your email address before logging in.")
                    }
                } else {
                    _authUiState.value = AuthUiState.Error("Login failed: User is null")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isEmpty()) {
                _authUiState.value = AuthUiState.Error("Please enter your email address first.")
                return@launch
            }

            _authUiState.value = AuthUiState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authUiState.value = AuthUiState.Error("Password reset email sent! Check your inbox.")
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Failed to send reset email: ${e.message}")
            }
        }
    }

    fun createListing(listing: ProduceListing, imageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val user = auth.currentUser

            if (user != null) {
                if (imageUri != null) {
                    MediaManager.get().upload(imageUri)
                        .unsigned("farmyukti_preset")
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
                val currentProfile = _userProfile.value
                val userName = currentProfile?.name ?: "Unknown Farmer"
                val userContact = currentProfile?.mobile ?: ""

                val newListing = listing.copy(
                    farmerId = uid,
                    id = newDocRef.id,
                    imageUrl = imageUrl,
                    farmerName = userName,
                    contactNumber = if (listing.contactNumber.isNotEmpty()) listing.contactNumber else userContact
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

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            try {
                db.collection("listings").document(listingId).delete().await()
                fetchListings() // Refresh
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Failed to delete listing: ${e.message}")
            }
        }
    }

    fun fetchListings() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("listings").get().await()
                val list = snapshot.toObjects(ProduceListing::class.java)
                _listings.value = list
                updateFavoriteListings()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getListingById(id: String): ProduceListing? {
        return _listings.value.find { it.id == id }
    }

    fun logout() {
        auth.signOut()
        _userRole.value = null
        _userProfile.value = null
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

    fun analyzePestImage(imageUri: Uri, onComplete: (String, String) -> Unit) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            delay(3000) // Simulate AI Processing Time
            _authUiState.value = AuthUiState.Idle
            onComplete("Fall Armyworm Detected", "High Severity. Recommended Treatment: Apply Neem Oil or Emamectin Benzoate.")
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

sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    object Success : VerificationState()
    data class Error(val message: String) : VerificationState()
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
            LoginScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.FarmerMain.route) {
            FarmerMainScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.CreateListing.route) {
            CreateListingScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.FarmerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.FARMER)
        }

        composable(Screen.BuyerMain.route) {
            BuyerMainScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.BuyerNegotiation.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: "default"
            NegotiationScreen(navController = navController, listingId = listingId, userRole = UserRole.BUYER)
        }

        composable(Screen.ListingDetail.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            if (listingId != null) {
                val listing = appViewModel.getListingById(listingId)
                if (listing != null) {
                    ListingDetailScreen(navController = navController, listing = listing, appViewModel = appViewModel)
                }
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                appViewModel = appViewModel
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController, appViewModel = appViewModel)
        }

        composable(Screen.Mandi.route) {
            MandiScreen(navController = navController)
        }

        composable(Screen.FarmerAdvisary.route){
            FarmerAdvisoryScreen(navController )
        }
        composable(Screen.PestControl.route){
            PlantDiagnosisScreen(navController)
        }
        composable(Screen.CropRec.route){
            CropRecommendationScreen(navController)
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
                        null -> { }
                    }
                }
                is AuthUiState.SignedOut -> {
                    onNavigate(Screen.Auth.route)
                }
                is AuthUiState.Error -> {
                    onNavigate(Screen.Auth.route)
                }
                else -> { }
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
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 18.sp)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSignUpClicked,
            modifier = Modifier.fillMaxWidth().height(60.dp),
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
            Toast.makeText(context, "Sign up successful! Please check your email to verify account.", Toast.LENGTH_LONG).show()
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

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = {
                    appViewModel.resetPassword(email)
                }) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                }
            }

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
        }
    }
}

@Composable
fun FarmerMainScreen(
    navController: NavController,
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
                    FarmerHomeScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.FarmerListings.route) {
                    FarmerListingsScreen(navController = navController, appViewModel = appViewModel)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.FarmerAdvisary.route) {
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
        Screen.FarmerAdvisary,
        Screen.Profile
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
                    // --- CORRECTION STARTS HERE ---
                    // We removed the if(screen.route == Advisory) check.
                    // Now ALL screens use the optimized navigation logic.
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    // --- CORRECTION ENDS HERE ---
                }
            )
        }
    }
}

@Composable
fun FarmerHomeScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {

    val userProfile by appViewModel.userProfile.collectAsState()
    val userName = userProfile?.name ?: "Farmer"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar with greeting
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
                    Text("Hi $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp).clickable { navController.navigate(Screen.Profile.route) },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
        }

       item {
           AutoSlidingBanner(DataModelist,onBannerClick = { clickedBanner ->
               println("Clicked on: ${clickedBanner.title}")
           })
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
                QuickActionCard(title = "Crop Recommendation", icon = Icons.AutoMirrored.Filled.Chat, onClick = {navController.navigate(
                    Screen.CropRec.route) })

                QuickActionCard(title = "Pest Scan", icon = Icons.Filled.DocumentScanner, onClick = { navController.navigate(
                    Screen.PestControl.route) })
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionCard(title = "Market Prices", icon = Icons.Filled.TrendingUp, onClick = { navController.navigate(Screen.Mandi.route) })

                QuickActionCard(title = "Favourites", icon = Icons.Default.Favorite, onClick = {
                    navController.navigate(Screen.Favorites.route)
                })

                QuickActionCard(title = "My Profile", icon = Icons.Filled.AccountCircle, onClick = { navController.navigate(Screen.Profile.route) })
            }
        }
    }
}

@Composable
fun BuyerMainScreen(
    navController: NavController,
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
                    BuyerHomeScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.BuyerMarketplace.route) {
                    BuyerMarketplaceScreen(navController = navController, appViewModel = appViewModel)
                }
                composable(Screen.BuyerTracking.route) {
                    BuyerTrackingScreen(navController = navController)
                }
                composable(Screen.FarmerAdvisary.route) {
                    FarmerAdvisoryScreen(navController = navController)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(navController = navController, appViewModel = appViewModel)
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
        Screen.BuyerTracking,
        Screen.Profile // Added Profile
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
    appViewModel: AppViewModel
) {
    val featuredCategories = listOf("Rice", "Spices", "Pulses", "Fruits", "Vegetables")
    val recentOrders by appViewModel.listings.collectAsState()
    val userProfile by appViewModel.userProfile.collectAsState()
    val userName = userProfile?.name ?: "Buyer"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar with greeting
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
                    Text("Hi $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp).clickable { navController.navigate(Screen.Profile.route) },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        item {
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
                    navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                showChatButton = true
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
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, appViewModel: AppViewModel) {
    val userProfile by appViewModel.userProfile.collectAsState()
    val verificationStatus by appViewModel.verificationStatus.collectAsState()

    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var mobile by remember { mutableStateOf(userProfile?.mobile ?: "") }
    var agriStackId by remember { mutableStateOf(userProfile?.agriStackId ?: "") }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    // No back icon if opened from bottom bar, but good to have if pushed
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Pic",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))
            Text(userProfile?.email ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(userProfile?.role ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, "") }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, "") }
            )

            if (userProfile?.role == "FARMER") {
                Spacer(Modifier.height(16.dp))
                // AgriStack ID Section with Verification
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = agriStackId,
                        onValueChange = { agriStackId = it },
                        label = { Text("AgriStack Farmer ID") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.AccountCircle, "") }
                    )

                    Spacer(Modifier.width(8.dp))

                    if (userProfile?.isVerified == true) {
                        Icon(Icons.Default.Verified, "Verified", tint = Color.Blue, modifier = Modifier.size(32.dp))
                    } else {
                        Button(
                            onClick = { appViewModel.verifyAgriStackId(agriStackId) },
                            enabled = verificationStatus !is VerificationState.Loading
                        ) {
                            if (verificationStatus is VerificationState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Verify")
                            }
                        }
                    }
                }
                if (verificationStatus is VerificationState.Error) {
                    Text(
                        text = (verificationStatus as VerificationState.Error).message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (verificationStatus is VerificationState.Success) {
                    Text(
                        text = "Verification Successful!",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    appViewModel.updateUserProfile(name, mobile, agriStackId) {
                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Save Profile")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    appViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true } // Clear entire stack
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Log Out")
            }
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
                    onClick = {
                        navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                    },
                    onDelete = {
                        appViewModel.deleteListing(listing.id)
                    }
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
    showChatButton: Boolean = false,
    onDelete: (() -> Unit)? = null
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
            if (listing.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = listing.imageUrl,
                    contentDescription = listing.produceName,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = listing.produceName,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(listing.produceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Qty: ${listing.quantityKg} kg", style = MaterialTheme.typography.bodyMedium)
                Text("Price: ₹${listing.basePricePerKg}/kg", style = MaterialTheme.typography.bodyMedium)
                Text("Loc: ${listing.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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

                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
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
                        Icon(Icons.AutoMirrored.Filled.Chat, "Chat", tint = Color(0xFF25D366))
                    }
                }
            }
        }
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
                        navController.navigate(Screen.ListingDetail.createRoute(listing.id))
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

// --- NEW: Listing Detail Screen with Favorites ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(navController: NavController, listing: ProduceListing, appViewModel: AppViewModel) {
    val context = LocalContext.current
    // Observe profile to check favorite status
    val userProfile by appViewModel.userProfile.collectAsState()
    val isFavorite = userProfile?.favorites?.contains(listing.id) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listing.produceName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Favorite Toggle Icon
                    IconButton(onClick = { appViewModel.toggleFavorite(listing.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
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
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            if (listing.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = listing.imageUrl,
                    contentDescription = listing.produceName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, "No Image", tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(listing.produceName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Sold by: ${listing.farmerName}", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, "", tint = Color(0xFFFFD700))
                Text(" ${listing.rating} Rating", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(listing.aiQualityGrade, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            DetailRow(Icons.Default.AttachMoney, "Price", "₹${listing.basePricePerKg}/kg")
            DetailRow(Icons.Default.ShoppingCart, "Quantity Available", "${listing.quantityKg} kg")
            DetailRow(Icons.Default.GpsFixed, "Location", listing.location)

            Spacer(Modifier.height(16.dp))
            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(listing.description.ifEmpty { "No description provided." }, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://api.whatsapp.com/send?phone=${listing.contactNumber}")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)) // WhatsApp Green
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", modifier = Modifier.padding(end = 8.dp))
                Text("Chat on WhatsApp")
            }
        }
    }
}

// --- NEW: Favorites Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, appViewModel: AppViewModel) {
    val favoriteListings by appViewModel.favoriteListings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favourites") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (favoriteListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No favorites yet!", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteListings) { listing ->
                    ProduceListItem(
                        listing = listing,
                        onClick = {
                            navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                        },
                        showChatButton = true
                    )
                }
            }
        }
    }
}
//object FarmerAdvisory : Screen("farmer_advisory", "Advisory", Icons.Default.Eco)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerAdvisoryScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Rainfall", "Pest", "Fertilizer", "Sowing")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advisory Services") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            androidx.compose.material3.TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    androidx.compose.material3.Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, maxLines = 1) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> RainfallAdvisoryContent()
                1 -> PestAdvisoryContent(navController)
                2 -> FertilizerAdvisoryContent()
                3 -> SowingAdvisoryContent()
            }
        }
    }
}

@Composable
fun RainfallAdvisoryContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdvisoryCard(
            Advisory(
                id = "r1",
                title = "Heavy Rain Alert",
                type = AdvisoryType.WEATHER,
                summary = "Heavy rainfall expected in next 48 hours. Ensure proper drainage in fields.",
                date = "Nov 25, 2025"
            )
        )
        AdvisoryCard(
            Advisory(
                id = "r2",
                title = "Irrigation Advisory",
                type = AdvisoryType.WEATHER,
                summary = "Due to forecasted rain, pause irrigation for the next 3 days to save water.",
                date = "Nov 25, 2025"
            )
        )
    }
}

@Composable
fun AdvisoryCard(advisory: Advisory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when(advisory.type) {
                    AdvisoryType.WEATHER -> Icons.Default.WaterDrop
                    AdvisoryType.PEST -> Icons.Default.PestControl
                    AdvisoryType.FERTILIZER -> Icons.Default.Eco
                    AdvisoryType.CROP -> Icons.Default.Eco
                    else -> Icons.Default.Warning
                },
                contentDescription = "Advisory Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = advisory.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = advisory.summary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = advisory.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PestAdvisoryContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { navController.navigate(Screen.PestControl.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.DocumentScanner, contentDescription = "Scan", modifier = Modifier.padding(end = 8.dp))
            Text("AI Pest Scan")
        }

        AdvisoryCard(
            Advisory(
                id = "p1",
                title = "Fall Armyworm Alert",
                type = AdvisoryType.PEST,
                summary = "High risk of Fall Armyworm in Maize. Check for egg masses on leaves.",
                date = "Nov 24, 2025"
            )
        )
        AdvisoryCard(
            Advisory(
                id = "p2",
                title = "Aphid Control",
                type = AdvisoryType.PEST,
                summary = "Use Neem oil spray (5ml/L) for controlling early aphid infestation.",
                date = "Nov 20, 2025"
            )
        )
    }
}

@Composable
fun FertilizerAdvisoryContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdvisoryCard(
            Advisory(
                id = "f1",
                title = "Wheat Fertilizer Schedule",
                type = AdvisoryType.FERTILIZER,
                summary = "Apply first dose of Urea (40kg/acre) 21 days after sowing after first irrigation.",
                date = "Nov 25, 2025"
            )
        )
        AdvisoryCard(
            Advisory(
                id = "f2",
                title = "Soil Health Card",
                type = AdvisoryType.FERTILIZER,
                summary = "Your soil is low in Zinc. Apply Zinc Sulphate (10kg/acre) before sowing.",
                date = "Oct 15, 2025"
            )
        )
    }
}

@Composable
fun SowingAdvisoryContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AdvisoryCard(
            Advisory(
                id = "s1",
                title = "Wheat Sowing Window",
                type = AdvisoryType.CROP,
                summary = "Optimal time for sowing Wheat (HD-2967) is Nov 1 to Nov 15. Delay may reduce yield.",
                date = "Nov 01, 2025"
            )
        )
        AdvisoryCard(
            Advisory(
                id = "s2",
                title = "Seed Treatment",
                type = AdvisoryType.CROP,
                summary = "Treat seeds with Bavistin (2g/kg) to prevent fungal diseases.",
                date = "Oct 28, 2025"
            )
        )
    }
}

