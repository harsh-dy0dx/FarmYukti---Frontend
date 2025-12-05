package com.example.farmyukti

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, appViewModel: AppViewModel) {
    val userProfile by appViewModel.userProfile.collectAsState()
    val verificationStatus by appViewModel.verificationStatus.collectAsState()
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var mobile by remember { mutableStateOf(userProfile?.mobile ?: "") }
    var agriStackId by remember { mutableStateOf(userProfile?.agriStackId ?: "") }
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("My Profile") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AccountCircle, "Pic", modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(userProfile?.email ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(userProfile?.role ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Person, "") })
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), leadingIcon = { Icon(Icons.Default.Phone, "") })
            if (userProfile?.role == "FARMER") {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = agriStackId, onValueChange = { agriStackId = it }, label = { Text("AgriStack ID") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Spacer(Modifier.width(8.dp))
                    if (userProfile?.isVerified == true) Icon(Icons.Default.Verified, "Verified", tint = Color.Blue, modifier = Modifier.size(32.dp))
                    else Button(onClick = { appViewModel.verifyAgriStackId(agriStackId) }, enabled = verificationStatus !is VerificationState.Loading) { if (verificationStatus is VerificationState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Verify") }
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = { appViewModel.updateUserProfile(name, mobile, agriStackId) { Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show() } }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Save Profile") }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { appViewModel.logout(); navController.navigate(Screen.Auth.route) { popUpTo(0) { inclusive = true } } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.padding(end = 8.dp)); Text("Log Out") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(navController: NavController, listing: ProduceListing, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val userProfile by appViewModel.userProfile.collectAsState()
    val isFavorite = userProfile?.favorites?.contains(listing.id) == true

    Scaffold(topBar = { TopAppBar(title = { Text(listing.produceName) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, actions = { IconButton(onClick = { appViewModel.toggleFavorite(listing.id) }) { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Fav", tint = if (isFavorite) Color.Red else Color.Gray) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            AsyncImage(model = listing.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(16.dp))
            Text(listing.produceName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Sold by: ${listing.farmerName}", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Row { Icon(Icons.Default.Star, "", tint = Color(0xFFFFD700)); Text(" ${listing.rating} Rating"); Spacer(Modifier.width(16.dp)); Box(Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)).padding(8.dp)) { Text(listing.aiQualityGrade, color = Color.White) } }
            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            Text("₹${listing.basePricePerKg}/kg", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(listing.description)
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("https://api.whatsapp.com/send?phone=${listing.contactNumber}") }
                try { context.startActivity(intent) } catch (e: Exception) { Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show() }
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Icon(Icons.AutoMirrored.Filled.Chat, null); Text("Chat on WhatsApp") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, appViewModel: AppViewModel) {
    val favoriteListings by appViewModel.favoriteListings.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("My Favourites") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(favoriteListings) { listing -> ProduceListItem(listing = listing, onClick = { navController.navigate(Screen.ListingDetail.createRoute(listing.id)) }, showChatButton = true) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegotiationScreen(navController: NavController, listingId: String, userRole: UserRole) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(NegotiationMessage("1", "Farmer", "Price is 50", 0)) }
    Scaffold(topBar = { TopAppBar(title = { Text("Negotiation") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }, bottomBar = {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = messageText, onValueChange = { messageText = it }, modifier = Modifier.weight(1f))
            IconButton(onClick = { messages.add(NegotiationMessage("2", if(userRole==UserRole.FARMER) "Farmer" else "Buyer", messageText, 0)); messageText = "" }) { Icon(Icons.AutoMirrored.Filled.Send, "Send") }
        }
    }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(messages) { msg -> Text("${msg.sender}: ${msg.text}", modifier = Modifier.padding(vertical = 4.dp)) }
        }
    }
}