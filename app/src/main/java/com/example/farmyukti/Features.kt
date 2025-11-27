package com.example.farmyukti

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

public const val GEMINI_API_KEY = "AIzaSyDTX4u3Afxg0vyezUb4xQRib8WyWWamwEc"

@Composable
fun CropRecommendationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,

    ) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()



    // State for Input Fields
    var nitrogen by remember { mutableStateOf("90") }
    var phosphorus by remember { mutableStateOf("42") }
    var potassium by remember { mutableStateOf("43") }
    var phLevel by remember { mutableStateOf("6.5") }
    var rainfall by remember { mutableStateOf("200") }
    var temperature by remember { mutableStateOf("28") }
    var humidity by remember { mutableStateOf("80") }

    // State for Results
    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Crop Advisor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF16A34A),
            modifier = Modifier.padding(bottom = 16.dp)
        )



        // Input Grid
        Text("Soil Conditions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CompactInput(label = "N", value = nitrogen, onValueChange = { nitrogen = it }, modifier = Modifier.weight(1f))
            CompactInput(label = "P", value = phosphorus, onValueChange = { phosphorus = it }, modifier = Modifier.weight(1f))
            CompactInput(label = "K", value = potassium, onValueChange = { potassium = it }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CompactInput(label = "pH", value = phLevel,onValueChange = { phLevel = it }, modifier = Modifier.weight(1f))
            CompactInput(label = "Rain (mm)", value = rainfall, onValueChange = { rainfall = it }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CompactInput(label = "Temp (°C)", value = temperature, onValueChange = { temperature = it }, modifier = Modifier.weight(1f))
            CompactInput(label = "Humidity (%)", value = humidity, onValueChange = { humidity = it }, modifier = Modifier.weight(1f))
        }

        // Error Display
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }
        }

        // Action Button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {

                isLoading = true
                errorMessage = null
                resultText = null

                scope.launch {
                    try {
                        val prompt = createCropPrompt(nitrogen, phosphorus, potassium, phLevel, rainfall, temperature, humidity)

                        // Direct HTTP Call (No SDK)
                        val response = makeGeminiApiCall(GEMINI_API_KEY, prompt)
                        resultText = response

                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "Unknown error"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing...")
            } else {
                Text("Get Recommendation")
            }
        }

        // Result Card
        if (resultText != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recommendation:", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(resultText!!, fontSize = 14.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}


@Composable
fun PlantDiagnosisScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onAnalysisComplete: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // --- State ---
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var promptText by remember { mutableStateOf("dont give any special charater in reply,  Identify this plant and diagnose any pest or disease issues visible. Provide organic and chemical control recommendations in Hindi language, also reply in formal way and short reply,dont give any special character just formal reply.") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // --- Image Picker Launcher ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            responseText = ""
            errorMessage = null

            // Decode URI to Bitmap
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                errorMessage = "Failed to load image: ${e.localizedMessage}"
            }
        }
    }

    // --- UI Layout ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PlantDoctor AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Image Selection Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedBitmap != null) {
                Image(
                    bitmap = selectedBitmap!!.asImageBitmap(),
                    contentDescription = "Selected Plant",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 'Remove' Button
                IconButton(
                    onClick = {
                        selectedImageUri = null
                        selectedBitmap = null
                        responseText = ""
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Upload",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Text("Tap to upload plant photo", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))

        // Action Button
        Button(
            onClick = {
                if (selectedBitmap != null && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    responseText = ""

                    scope.launch {
                        try {
                            val base64Image = bitmapToBase64(selectedBitmap!!)
                            val result = callGeminiApi(promptText, base64Image)
                            responseText = result
                            onAnalysisComplete?.invoke(result)
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else if (selectedBitmap == null) {
                    errorMessage = "Please upload an image first."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if(isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Diagnose Plant")
            }
        }

        // Error Display
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Result Display
        if (responseText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Diagnosis Result:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            SelectionContainer {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = responseText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerAdvisoryScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Rainfall", "Pest", "Fertilizer", "Sowing")
    Scaffold(topBar = { TopAppBar(title = { Text("Advisory Services") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title -> Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title) }) }
            }
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                when (selectedTabIndex) {
                    0 -> { AdvisoryCard(Advisory("r1", "Heavy Rain", AdvisoryType.WEATHER, "Heavy rain expected.", "Nov 25")) }
                    1 -> { Button(onClick = { navController.navigate(Screen.PestControl.route) }) { Text("AI Pest Scan") }; AdvisoryCard(Advisory("p1", "Armyworm", AdvisoryType.PEST, "Check maize.", "Nov 24")) }
                    2 -> { AdvisoryCard(Advisory("f1", "Urea", AdvisoryType.FERTILIZER, "Apply Urea.", "Nov 25")) }
                    3 -> { AdvisoryCard(Advisory("s1", "Wheat", AdvisoryType.CROP, "Sowing time.", "Nov 01")) }
                }
            }
        }
    }
}

// --- Helpers ---
//@Composable
//fun CompactInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
//    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
//        Column(Modifier.padding(8.dp)) { Text(label, fontSize = 10.sp, color = Color.Gray); BasicTextField(value = value, onValueChange = onValueChange, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
//    }
//}



@Composable
fun CompactInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}






private suspend fun bitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Compress to JPEG, 80% quality to save bandwidth and fit API limits
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

private suspend fun callGeminiApi(prompt: String, base64Image: String): String = withContext(Dispatchers.IO) {
    if (GEMINI_API_KEY.isEmpty()) throw Exception("API Key is missing.")

    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=$GEMINI_API_KEY")

    // Construct JSON Payload using standard org.json
    val jsonBody = JSONObject().apply {
        put("contents", JSONArray().apply {
            put(JSONObject().apply {
                put("parts", JSONArray().apply {
                    // 1. Text Prompt
                    put(JSONObject().put("text", prompt))
                    // 2. Image Data
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64Image)
                        })
                    })
                })
            })
        })
    }

    (url.openConnection() as HttpURLConnection).run {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        doOutput = true

        outputStream.use { os ->
            val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
            os.write(input, 0, input.size)
        }

        val responseCode = responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream.bufferedReader().use { reader ->
                val response = reader.readText()
                val jsonResponse = JSONObject(response)

                // Navigate: candidates[0] -> content -> parts[0] -> text
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@use parts.getJSONObject(0).optString("text", "No text response found.")
                    }
                }
                return@use "No viable candidate returned from API."
            }
        } else {
            val errorStream = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            throw Exception("API Error ($responseCode): $errorStream")
        }
    }
}
private suspend fun makeGeminiApiCall(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
    // Using gemini-1.5-flash which is stable
    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=$GEMINI_API_KEY")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true

    // Construct JSON Body: {"contents": [{"parts": [{"text": "..."}]}]}
    val jsonBody = JSONObject().apply {
        put("contents", JSONArray().put(
            JSONObject().put("parts", JSONArray().put(
                JSONObject().put("text", prompt)
            ))
        ))
    }

// Write Request Body
    OutputStreamWriter(conn.outputStream).use { writer ->
        writer.write(jsonBody.toString())
    }

    // Read Response
    val responseCode = conn.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()

        // Parse Response: candidates[0].content.parts[0].text
        val jsonResponse = JSONObject(response.toString())
        try {
            jsonResponse.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            "Error parsing result: ${e.message}"
        }
    } else {
        // Read error details
        val errorReader = BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream))
        val errorResponse = errorReader.readText()
        throw Exception("API Error ($responseCode): $errorResponse")
    }
}

// Helper to format the prompt
private fun createCropPrompt(n: String, p: String, k: String, ph: String, rain: String, temp: String, hum: String): String {
    return """
        return in hindi language,
        don't return any special character, and give only in formal reply for professional and in short reply to the point reply
        Recommend a crop for these conditions:
        N:$n, P:$p, K:$k, pH:$ph, Rain:${rain}mm, Temp:${temp}C, Humidity:$hum%
        Return: 1. Crop Name 2. Why it fits 3. Tips.
    """.trimIndent()
}

