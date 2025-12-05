package com.example.farmyukti

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.cloudinary.android.callback.UploadCallback
import com.example.farmyukti.model.WeatherResponse
import com.example.farmyukti.repo.RetrofitClientWeather
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object SignedIn : AuthUiState()
    object SignedOut : AuthUiState()
    object SignUpSuccess : AuthUiState()
    data class Error(val message: String?) : AuthUiState()
}
sealed class ImageUploadState {
    object Idle : ImageUploadState()
    object Loading : ImageUploadState()
    object Success : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}

sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    object Success : VerificationState()
    data class Error(val message: String) : VerificationState()
}

class AppViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val currentUserId: String? get() = auth.currentUser?.uid

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    private val _imageUploadStatus = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadStatus: StateFlow<ImageUploadState> = _imageUploadStatus

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _verificationStatus = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationStatus: StateFlow<VerificationState> = _verificationStatus

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _listings = MutableStateFlow<List<ProduceListing>>(emptyList())
    val listings: StateFlow<List<ProduceListing>> = _listings.asStateFlow()

    private val _favoriteListings = MutableStateFlow<List<ProduceListing>>(emptyList())
    val favoriteListings: StateFlow<List<ProduceListing>> = _favoriteListings.asStateFlow()

    init {
        checkCurrentUser()
        fetchListings()
    }

    // --- Search & Filter Logic ---
    // In AppViewModel.kt -> replace the existing filterListings function with this:

    fun filterListings(
        originalList: List<ProduceListing>,
        query: String,
        category: String?,
        grade: String?,
        location: String?
    ): List<ProduceListing> {
        return originalList.filter { item ->
            // 1. Search Query
            val matchesSearch = if (query.isBlank()) true else {
                item.produceName.contains(query, ignoreCase = true) ||
                        item.farmerName.contains(query, ignoreCase = true)
            }

            // 2. Category Filter (CHANGED TO CONTAINS)
            val matchesCategory = if (category.isNullOrBlank() || category == "All") true else {
                item.produceName.contains(category, ignoreCase = true) // <--- Fixed here
            }

            // 3. Grade Filter
            val matchesGrade = if (grade.isNullOrBlank() || grade == "All") true else {
                item.aiQualityGrade.equals(grade, ignoreCase = true)
            }

            // 4. Location Filter
            val matchesLocation = if (location.isNullOrBlank() || location == "All") true else {
                item.location.contains(location, ignoreCase = true)
            }

            matchesSearch && matchesCategory && matchesGrade && matchesLocation
        }
    }

    // Helper to get unique values for Dropdowns
    fun getUniqueLocations(listings: List<ProduceListing>): List<String> =
        listOf("All") + listings.map { it.location }.distinct().sorted()

    fun getUniqueCategories(listings: List<ProduceListing>): List<String> =
        listOf("All") + listings.map { it.produceName }.distinct().sorted()

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

    fun uploadProfileImage(context: Context, uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        _imageUploadStatus.value = ImageUploadState.Loading

        MediaManager.get().upload(uri)
            .unsigned("farmyukti_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    // 1. Safety Fix: Prevent crash if key is missing or null
                    val downloadUrl = resultData["secure_url"] as? String

                    if (downloadUrl == null) {
                        _imageUploadStatus.value = ImageUploadState.Error("Upload failed: No URL returned")
                        return
                    }

                    // 2. Threading Fix: Toasts MUST run on the Main Thread
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }

                    // 3. Database Update
                    db.collection("users").document(uid)
                        .update("photoUrl", downloadUrl)
                        .addOnSuccessListener {
                            // 4. The "Reflect Back" Fix:
                            // This pulls the new data from DB, updating the variable your UI observes
                            fetchUserData(uid)
                            _imageUploadStatus.value = ImageUploadState.Success
                        }
                        .addOnFailureListener { e ->
                            _imageUploadStatus.value = ImageUploadState.Error("Firestore update failed: ${e.message}")
                        }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    _imageUploadStatus.value = ImageUploadState.Error(error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
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
                val updates = mapOf("name" to name, "mobile" to mobile, "agriStackId" to agriStackId)
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
            // 1. Strict Input Validation (11 Digits only)
            if (!agriStackId.matches(Regex("^\\d{11}$"))) {
                _verificationStatus.value = VerificationState.Error("ID must be exactly 11 digits")
                return@launch
            }

            _verificationStatus.value = VerificationState.Loading
            val currentUser = auth.currentUser

            if (currentUser == null) {
                _verificationStatus.value = VerificationState.Error("User not logged in")
                return@launch
            }

            try {
                val agriRef = db.collection("agristack_ids").document(agriStackId)
                val userRef = db.collection("users").document(currentUser.uid)

                // 2. Use a Transaction for Safety (Atomic Operation)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(agriRef)

                    if (!snapshot.exists()) {
                        throw FirebaseFirestoreException("Invalid AgriStack ID", FirebaseFirestoreException.Code.ABORTED)
                    }

                    val linkedEmail = snapshot.getString("linked_email")

                    // Check if already linked to SOMEONE ELSE
                    if (!linkedEmail.isNullOrEmpty() && linkedEmail != currentUser.email) {
                        throw FirebaseFirestoreException("ID already linked to another account", FirebaseFirestoreException.Code.ALREADY_EXISTS)
                    }

                    // If not linked, or linked to THIS user (re-verification), proceed
                    transaction.update(agriRef, "linked_email", currentUser.email)
                    transaction.update(userRef, "isVerified", true)
                    transaction.update(userRef, "agriStackId", agriStackId) // Also save the ID to user profile

                    true // Return true to confirm success
                }.await()

                // 3. Success State
                fetchUserData(currentUser.uid) // Refresh local data
                _verificationStatus.value = VerificationState.Success

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseFirestoreException -> e.message ?: "Verification error"
                    else -> "Connection failed: ${e.message}"
                }
                _verificationStatus.value = VerificationState.Error(errorMessage)
            }
        }
    }


    fun toggleFavorite(listingId: String) {
        val user = auth.currentUser ?: return
        val currentFavorites = _userProfile.value?.favorites ?: emptyList()
        val newFavorites = if (currentFavorites.contains(listingId)) currentFavorites - listingId else currentFavorites + listingId
        viewModelScope.launch {
            try {
                db.collection("users").document(user.uid).update("favorites", newFavorites).await()
                fetchUserData(user.uid)
            } catch (e: Exception) { }
        }
    }

    fun signUp(email: String, password: String, role: UserRole, farmerId: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if(user != null) {
                    user.sendEmailVerification().await()
                    val profile = UserProfile(uid = user.uid, email = email, role = role.name, agriStackId = farmerId, isVerified = farmerId.isNotEmpty())
                    db.collection("users").document(user.uid).set(profile).await()
                    auth.signOut()
                    _authUiState.value = AuthUiState.SignUpSuccess
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
                    if (user.isEmailVerified) fetchUserData(user.uid)
                    else {
                        auth.signOut()
                        _authUiState.value = AuthUiState.Error("Please verify your email.")
                    }
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try { auth.sendPasswordResetEmail(email).await(); _authUiState.value = AuthUiState.Error("Email sent!") }
            catch (e: Exception) { _authUiState.value = AuthUiState.Error(e.message) }
        }
    }



    fun createListing(listing: ProduceListing, imageUris: List<Uri>, onComplete: () -> Unit) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val user = auth.currentUser

            if (user != null) {
                if (imageUris.isNotEmpty()) {
                    try {
                        // Upload all images and wait for the results
                        val uploadedUrls = uploadAllImages(imageUris)

                        // Once all images are uploaded, save to Firestore
                        saveListingToFirestore(listing, uploadedUrls, user.uid, onComplete)

                    } catch (e: Exception) {
                        _authUiState.value = AuthUiState.Error("Image upload failed: ${e.message}")
                    }
                } else {
                    // No images selected, save with empty list
                    saveListingToFirestore(listing, emptyList(), user.uid, onComplete)
                }
            }
        }
    }




    // 2. Helper function to upload multiple images and return their URLs
    private suspend fun uploadAllImages(uris: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        val uploadedUrls = mutableListOf<String>()

        // We use 'map' + 'awaitAll' pattern (or simple loop with suspendCoroutine) to handle async uploads
        // Here is a simple approach using suspendCancellableCoroutine for Cloudinary callback
        uris.forEach { uri ->
            val url = uploadSingleImage(uri)
            if (url != null) {
                uploadedUrls.add(url)
            }
        }
        return@withContext uploadedUrls
    }

    // 3. Helper to wrap the Cloudinary callback in a coroutine
    private suspend fun uploadSingleImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri).unsigned("farmyukti_preset").callback(object : UploadCallback {
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val imageUrl = resultData["secure_url"] as? String
                if (continuation.isActive) {
                    continuation.resume(imageUrl)
                }
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                // Log error or handle it. For now, we resume with null to skip this image
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }




    // 4. Update Firestore save function to handle List<String>
    private fun saveListingToFirestore(listing: ProduceListing, imageUrls: List<String>, uid: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val newDocRef = db.collection("listings").document()
                val profile = _userProfile.value

                val newListing = listing.copy(
                    farmerId = uid,
                    id = newDocRef.id,
                    // Save the LIST of URLs
                    imageUrls = imageUrls,
                    // You can keep a main 'imageUrl' for backward compatibility (using the first one)
                    imageUrl = imageUrls.firstOrNull() ?: "",

                    farmerName = profile?.name ?: "Farmer",
                    contactNumber = if(listing.contactNumber.isNotEmpty()) listing.contactNumber else profile?.mobile ?: ""
                )

                newDocRef.set(newListing).await()
                fetchListings()
                _authUiState.value = AuthUiState.Idle
                onComplete()
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Save failed: ${e.message}")
            }
        }
    }
    //*********************************************************************





    private fun saveListingToFirestore(listing: ProduceListing, imageUrl: String, uid: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val newDocRef = db.collection("listings").document()
                val profile = _userProfile.value
                val newListing = listing.copy(
                    farmerId = uid, id = newDocRef.id, imageUrl = imageUrl,
                    farmerName = profile?.name ?: "Farmer",
                    contactNumber = if(listing.contactNumber.isNotEmpty()) listing.contactNumber else profile?.mobile ?: ""
                )
                newDocRef.set(newListing).await()
                fetchListings()
                _authUiState.value = AuthUiState.Idle
                onComplete()
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Save failed: ${e.message}")
            }
        }
    }

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            try { db.collection("listings").document(listingId).delete().await(); fetchListings() } catch (e: Exception) {}
        }
    }

    fun fetchListings() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("listings").get().await()
                _listings.value = snapshot.toObjects(ProduceListing::class.java)
                updateFavoriteListings()
            } catch (e: Exception) {}
        }
    }

    fun getListingById(id: String): ProduceListing? = _listings.value.find { it.id == id }

    fun logout() {
        auth.signOut()
        _userRole.value = null
        _userProfile.value = null
        _authUiState.value = AuthUiState.SignedOut
    }

    fun resetAuthState() {
        if (_authUiState.value is AuthUiState.Error || _authUiState.value is AuthUiState.SignUpSuccess) {
            _authUiState.value = if(auth.currentUser == null) AuthUiState.SignedOut else AuthUiState.Idle
        }
    }





    //weather

    // LiveData/State to hold the result
    var weatherData by mutableStateOf<WeatherResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Replace with your actual key from the API call screenshot: 5fa0c1fe2923498bbb33154728250512
    private val API_KEY = "YOUR_API_KEY_HERE"

    fun fetchWeather(city: String) {
        isLoading = true
        error = null

        viewModelScope.launch {
            try {
                // The parameters match your original API call structure
                val response = RetrofitClientWeather.weatherService.getCurrentWeather(
                    apiKey = API_KEY,
                    location = city,
                    includeAqi = "yes"
                )
                weatherData = response
            } catch (e: Exception) {
                error = "Failed to fetch weather: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}