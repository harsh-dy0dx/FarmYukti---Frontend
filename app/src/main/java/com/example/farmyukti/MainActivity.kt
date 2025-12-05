package com.example.farmyukti

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cloudinary.android.MediaManager
import com.example.farmyukti.repo.MandiScreen
import com.example.farmyukti.repo.MandiViewModel

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
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = "dhrqr1wiv"
            MediaManager.init(this, config)
        } catch (e: Exception) { }

        setContent {
            FarmyuktiTheme {
                FarmYuktiApp()

            }
        }
    }
}

@Composable
fun FarmYuktiApp(appViewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val authUiState by appViewModel.authUiState.collectAsState()
    val userRole by appViewModel.userRole.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Error) {
            Toast.makeText(context, (authUiState as AuthUiState.Error).message, Toast.LENGTH_SHORT).show()
            appViewModel.resetAuthState()
        }
    }

    NavHost(navController = navController, startDestination = Screen.Landing.route) {
        composable(Screen.Landing.route) { LandingScreen(authUiState, userRole) { dest -> navController.navigate(dest) { popUpTo(Screen.Landing.route) { inclusive = true }; launchSingleTop = true } } }
        composable(Screen.Auth.route) { AuthScreen(onLoginClicked = { navController.navigate(Screen.Login.route) }, onSignUpClicked = { navController.navigate(Screen.SignUp.route) }) }
        composable(Screen.Login.route) { LoginScreen(navController, appViewModel) }
        composable(Screen.SignUp.route) { SignUpScreen(navController, appViewModel) }
        composable(Screen.FarmerMain.route) { FarmerMainScreen(navController, appViewModel) }
        composable(Screen.CreateListing.route) { CreateListingScreen(navController, appViewModel) }
        //composable(Screen.FarmerNegotiation.route) { backStackEntry -> NegotiationScreen(navController, backStackEntry.arguments?.getString("listingId") ?: "default", UserRole.FARMER) }
        composable(Screen.BuyerMain.route) { BuyerMainScreen(navController, appViewModel) }
        //composable(Screen.BuyerNegotiation.route) { backStackEntry -> NegotiationScreen(navController, backStackEntry.arguments?.getString("listingId") ?: "default", UserRole.BUYER) }
        composable(Screen.ListingDetail.route) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            if (listingId != null) {
                val listing = appViewModel.getListingById(listingId)
                if (listing != null) ListingDetailScreen(navController, listing, appViewModel)
            }
        }
        composable(Screen.Profile.route) { ProfileScreen(navController, appViewModel) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController, appViewModel) }
        composable(Screen.Mandi.route) { MandiScreen(
            viewModel = MandiViewModel(),
            navController = navController
        ) }
        composable(Screen.FarmerAdvisary.route) { FarmerAdvisoryScreen(navController) }
        composable(Screen.PestControl.route) { PlantDiagnosisScreen(navController) }
        composable(Screen.CropRec.route) { CropRecommendationScreen(navController) }
    }
}