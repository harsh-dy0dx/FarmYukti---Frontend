package com.example.farmyukti

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LandingScreen(authUiState: AuthUiState, userRole: UserRole?, onNavigate: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
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
                is AuthUiState.SignedOut, is AuthUiState.Error -> onNavigate(Screen.Auth.route)
                else -> { }
            }
        }
    }
}

@Composable
fun AuthScreen(onLoginClicked: () -> Unit, onSignUpClicked: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Eco, "Logo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(100.dp))
        Spacer(Modifier.height(16.dp))
        Text("Welcome to FarmYukti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Direct Market Access for Farmers", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(Modifier.height(48.dp))
        Button(onClick = onLoginClicked, modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(12.dp)) { Text("Login", fontSize = 18.sp) }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onSignUpClicked, modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(12.dp)) { Text("Sign Up", fontSize = 18.sp) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, appViewModel: AppViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.SignedIn) {
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Landing.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Login") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Eco, "Logo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, leadingIcon = { Icon(Icons.Default.Email, "Email") })
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true, leadingIcon = { Icon(Icons.Default.Password, "Password") })
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { appViewModel.resetPassword(email) }) { Text("Forgot Password?", color = MaterialTheme.colorScheme.primary) }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { appViewModel.login(email, password) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !isLoading) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp)) else Text("Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, appViewModel: AppViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var farmerId by rememberSaveable { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    val authUiState by appViewModel.authUiState.collectAsState()
    val isLoading = authUiState is AuthUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.SignUpSuccess) {
            Toast.makeText(context, "Sign up successful!", Toast.LENGTH_LONG).show()
            appViewModel.resetAuthState()
            navController.navigate(Screen.Login.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Create Account") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2), onClick = { selectedRole = UserRole.FARMER }, selected = selectedRole == UserRole.FARMER) { Text("I am a Farmer") }
                SegmentedButton(shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2), onClick = { selectedRole = UserRole.BUYER }, selected = selectedRole == UserRole.BUYER) { Text("I am a Buyer") }
            }
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), leadingIcon = { Icon(Icons.Default.Email, "") })
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Password, "") })
            Spacer(Modifier.height(16.dp))
            if (selectedRole == UserRole.FARMER) {
                OutlinedTextField(value = farmerId, onValueChange = { farmerId = it }, label = { Text("11-Digit Farmer ID") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), leadingIcon = { Icon(Icons.Default.AccountCircle, "") })
                Spacer(Modifier.height(16.dp))
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = { appViewModel.signUp(email, password, selectedRole, farmerId) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !isLoading) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp)) else Text("Sign Up")
            }
        }
    }
}