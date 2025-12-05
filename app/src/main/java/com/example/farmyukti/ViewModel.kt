package com.example.farmyukti

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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

class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    val currentUserId: String? get() = auth.currentUser?.uid

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
                        _verificationStatus.value = VerificationState.Error("This ID is already linked to another account.")
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
                    _verificationStatus.value = VerificationState.Error("Invalid AgriStack ID.")
                }
            } catch (e: Exception) {
                _verificationStatus.value = VerificationState.Error("Verification failed: ${e.message}")
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

//    fun createListing(listing: ProduceListing, imageUri: Uri?, onComplete: () -> Unit) {
//        viewModelScope.launch {
//            _authUiState.value = AuthUiState.Loading
//            val user = auth.currentUser
//            if (user != null) {
//                if (imageUri != null) {
//                    MediaManager.get().upload(imageUri).unsigned("farmyukti_preset").callback(object : UploadCallback {
//                        override fun onStart(requestId: String) {}
//                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
//                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
//                            val imageUrl = resultData["secure_url"] as String
//                            saveListingToFirestore(listing, imageUrl, user.uid, onComplete)
//                        }
//                        override fun onError(requestId: String, error: ErrorInfo) {
//                            _authUiState.value = AuthUiState.Error("Upload failed")
//                        }
//                        override fun onReschedule(requestId: String, error: ErrorInfo) {}
//                    }).dispatch()
//                } else {
//                    saveListingToFirestore(listing, "", user.uid, onComplete)
//                }
//            }
//        }
//    }


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
}