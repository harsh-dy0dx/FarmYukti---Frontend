package com.example.farmyukti

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// --- Navigation Routes ---
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

// --- Data Models ---
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
    val imageUrl: String ="",
    val imageUrls: List<String> = emptyList()
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

data class BannerModel(
    val id: String,
    val title: String,
    val imageUrl: String,
    val backgroundColor: Color
)

enum class UserRole { FARMER, BUYER }