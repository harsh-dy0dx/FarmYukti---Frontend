//package com.example.farmyukti
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.util.Base64
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.selection.SelectionContainer
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.rounded.Spa
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.ByteArrayOutputStream
//import java.io.InputStream
//import java.net.HttpURLConnection
//import java.net.URL
//
//// --- Configuration ---
//// TODO: Ideally, pass this key into the function or inject it securely.
//public const val GEMINI_API_KEY = "AIzaSyDMNB6XKMMiRoHkPjuzwUMZbCb7Zdy8xdg"
//
///**
// * A standalone Composable that provides Plant Doctor functionality.
// * Can be embedded in any part of your existing application.
// *
// * @param modifier Modifiers to apply to the root layout.
// * @param onAnalysisComplete Optional callback when a diagnosis is received.
// */
//@Composable
//fun PlantDiagnosisScreen(
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    onAnalysisComplete: ((String) -> Unit)? = null
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val scrollState = rememberScrollState()
//
//    // --- State ---
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
//    var promptText by remember { mutableStateOf("dont give any special charater in reply,  Identify this plant and diagnose any pest or disease issues visible. Provide organic and chemical control recommendations in Hindi language, also reply in formal way and short reply,dont give any special character just formal reply.") }
//    var responseText by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    // --- Image Picker Launcher ---
//    val photoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//        if (uri != null) {
//            selectedImageUri = uri
//            responseText = ""
//            errorMessage = null
//
//            // Decode URI to Bitmap
//            try {
//                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//                selectedBitmap = BitmapFactory.decodeStream(inputStream)
//            } catch (e: Exception) {
//                errorMessage = "Failed to load image: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    // --- UI Layout ---
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(scrollState),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Header
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(bottom = 24.dp)
//        ) {
//            Icon(imageVector = Icons.Rounded.Spa,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.size(32.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "PlantDoctor AI",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//
//        // Image Selection Area
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(250.dp)
//                .clip(RoundedCornerShape(16.dp))
//                .background(Color.White)
//                .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
//                .clickable {
//                    photoPickerLauncher.launch(
//                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                    )
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            if (selectedBitmap != null) {
//                Image(
//                    bitmap = selectedBitmap!!.asImageBitmap(),
//                    contentDescription = "Selected Plant",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//                // 'Remove' Button
//                IconButton(
//                    onClick = {
//                        selectedImageUri = null
//                        selectedBitmap = null
//                        responseText = ""
//                    },
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(8.dp)
//                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
//                ) {
//                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
//                }
//            } else {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(
//                        Icons.Default.Add,
//                        contentDescription = "Upload",
//                        modifier = Modifier.size(48.dp),
//                        tint = Color.Gray
//                    )
//                    Text("Tap to upload plant photo", color = Color.Gray)
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Query Input
//
////        OutlinedTextField(
////            value = "",//promptText,
////            onValueChange = { promptText = it },
////            label = { Text("Ask about your plant") },
////            modifier = Modifier.fillMaxWidth(),
////            minLines = 3,
////            maxLines = 5,
////            enabled = !isLoading
////        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Action Button
//        Button(
//            onClick = {
//                if (selectedBitmap != null && !isLoading) {
//                    isLoading = true
//                    errorMessage = null
//                    responseText = ""
//
//                    scope.launch {
//                        try {
//                            val base64Image = bitmapToBase64(selectedBitmap!!)
//                            val result = callGeminiApi(promptText, base64Image)
//                            responseText = result
//                            onAnalysisComplete?.invoke(result)
//                        } catch (e: Exception) {
//                            errorMessage = "Error: ${e.localizedMessage}"
//                        } finally {
//                            isLoading = false
//                        }
//                    }
//                } else if (selectedBitmap == null) {
//                    errorMessage = "Please upload an image first."
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp),
//            enabled = !isLoading,
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            if(isLoading) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    modifier = Modifier.size(24.dp)
//                )
//            } else {
//                Text("Diagnose Plant")
//            }
//        }
//
//        // Error Display
//        if (errorMessage != null) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Card(
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
//            ) {
//                Text(
//                    text = errorMessage!!,
//                    color = MaterialTheme.colorScheme.onErrorContainer,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        }
//
//        // Result Display
//        if (responseText.isNotEmpty()) {
//            Spacer(modifier = Modifier.height(24.dp))
//            Text(
//                text = "Diagnosis Result:",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.Start)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            SelectionContainer {
//                Card(
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
//                ) {
//                    Text(
//                        text = responseText,
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodyMedium,
//                        lineHeight = 24.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
//// --- Helper Functions (Private to this file) ---
//
//private suspend fun bitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
//    val byteArrayOutputStream = ByteArrayOutputStream()
//    // Compress to JPEG, 80% quality to save bandwidth and fit API limits
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
//    val byteArray = byteArrayOutputStream.toByteArray()
//    Base64.encodeToString(byteArray, Base64.NO_WRAP)
//}
//
//private suspend fun callGeminiApi(prompt: String, base64Image: String): String = withContext(Dispatchers.IO) {
//    if (GEMINI_API_KEY.isEmpty()) throw Exception("API Key is missing.")
//
//    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=$GEMINI_API_KEY")
//
//    // Construct JSON Payload using standard org.json
//    val jsonBody = JSONObject().apply {
//        put("contents", JSONArray().apply {
//            put(JSONObject().apply {
//                put("parts", JSONArray().apply {
//                    // 1. Text Prompt
//                    put(JSONObject().put("text", prompt))
//                    // 2. Image Data
//                    put(JSONObject().apply {
//                        put("inlineData", JSONObject().apply {
//                            put("mimeType", "image/jpeg")
//                            put("data", base64Image)
//                        })
//                    })
//                })
//            })
//        })
//    }
//
//    (url.openConnection() as HttpURLConnection).run {
//        requestMethod = "POST"
//        setRequestProperty("Content-Type", "application/json")
//        doOutput = true
//
//        outputStream.use { os ->
//            val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
//            os.write(input, 0, input.size)
//        }
//
//        val responseCode = responseCode
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            inputStream.bufferedReader().use { reader ->
//                val response = reader.readText()
//                val jsonResponse = JSONObject(response)
//
//                // Navigate: candidates[0] -> content -> parts[0] -> text
//                val candidates = jsonResponse.optJSONArray("candidates")
//                if (candidates != null && candidates.length() > 0) {val content = candidates.getJSONObject(0).optJSONObject("content")
//                    val parts = content?.optJSONArray("parts")
//                    if (parts != null && parts.length() > 0) {
//                        return@use parts.getJSONObject(0).optString("text", "No text response found.")
//                    }
//                }
//                return@use "No viable candidate returned from API."
//            }
//        } else {
//            val errorStream = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
//            throw Exception("API Error ($responseCode): $errorStream")
//        }
//    }
//}