package com.example.farmyukti


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// SDK import removed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// This is the standalone feature you can use anywhere
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

// ---------------------------------------------------------
// Helper: Direct API Call Logic
// ---------------------------------------------------------
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
        dont return any special character, and give only in formal reply for professional and in short reply to the point reply
        Recommend a crop for these conditions:
        N:$n, P:$p, K:$k, pH:$ph, Rain:${rain}mm, Temp:${temp}C, Humidity:$hum%
        Return: 1. Crop Name 2. Why it fits 3. Tips.
    """.trimIndent()
}

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