package com.example.farmyukti

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.io.InputStream





public const val GEMINI_API_KEY = "AQ.Ab8RN6LIvXj786njf15JVl1NdSLCl7Ze5ImdfhY_8HoVaS5QLQ"

@Composable
fun CropRecommendationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // ORIGINAL STATE LOGIC REMAINS UNTOUCHED
    var nitrogen by remember { mutableStateOf("90") }
    var phosphorus by remember { mutableStateOf("42") }
    var potassium by remember { mutableStateOf("43") }
    var phLevel by remember { mutableStateOf("6.5") }
    var rainfall by remember { mutableStateOf("200") }
    var temperature by remember { mutableStateOf("28") }
    var humidity by remember { mutableStateOf("80") }

    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = nitrogen.isNotEmpty() && phosphorus.isNotEmpty() &&
            potassium.isNotEmpty() && phLevel.isNotEmpty() &&
            rainfall.isNotEmpty() && temperature.isNotEmpty() &&
            humidity.isNotEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
    ) {
        // --- FIGMA GRADIENT HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF16A34A),
                                Color(0xFF059669)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color.White)
                            }
                        }
                        Text(
                            text = "Crop Advisor",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Get crop recommendations based on soil conditions",
                        color = Color(0xFFD1FAE5),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SOIL CONDITIONS FORM (FIGMA UI) ---
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Soil Conditions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NutrientCard(label = "N", value = nitrogen, onValueChange = { nitrogen = it }, placeholder = "90", color = Color(0xFFDEEBFF), modifier = Modifier.weight(1f))
                    NutrientCard(label = "P", value = phosphorus, onValueChange = { phosphorus = it }, placeholder = "42", color = Color(0xFFF3E8FF), modifier = Modifier.weight(1f))
                    NutrientCard(label = "K", value = potassium, onValueChange = { potassium = it }, placeholder = "43", color = Color(0xFFFFEDD5), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SoilParameterCard(label = "pH", value = phLevel, onValueChange = { phLevel = it }, placeholder = "6.5", modifier = Modifier.weight(1f))
                    SoilParameterCard(label = "Rain (mm)", value = rainfall, onValueChange = { rainfall = it }, placeholder = "200", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SoilParameterCard(label = "Temp (°C)", value = temperature, onValueChange = { temperature = it }, placeholder = "28", modifier = Modifier.weight(1f))
                    SoilParameterCard(label = "Humidity (%)", value = humidity, onValueChange = { humidity = it }, placeholder = "80", modifier = Modifier.weight(1f))
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- ORIGINAL API CALL TRIGGERED HERE ---
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        resultText = null
                        scope.launch {
                            try {
                                val prompt = createCropPrompt(nitrogen, phosphorus, potassium, phLevel, rainfall, temperature, humidity)
                                val response = makeGeminiApiCall(GEMINI_API_KEY, prompt)
                                resultText = response
                            } catch (e: Exception) {
                                errorMessage = e.localizedMessage ?: "Unknown error"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A),
                        disabledContainerColor = Color(0xFF16A34A).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Soil...")
                    } else {
                        Icon(Icons.Default.TrendingUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Recommendation")
                    }
                }
            }
        }

        // --- ORIGINAL RESULT DISPLAY WRAPPED IN FIGMA CARD ---
        if (resultText != null) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Recommendation:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp)) {
                            Text(text = resultText!!, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF374151))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { resultText = null },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("New Analysis") }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
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

    // ORIGINAL STATE LOGIC REMAINS UNTOUCHED
    val selectedImages = remember { mutableStateListOf<Bitmap>() }
    var promptText by remember { mutableStateOf("in response, dont give any special character just formal reply, no any *(star symbol) or #(hash symbol),  Identify this plant and diagnose any pest or disease issues visible. Provide organic and chemical control recommendations in Hindi language, also reply in formal way and short reply.") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        if (uris.isNotEmpty()) {
            errorMessage = null
            uris.forEach { uri ->
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null && selectedImages.size < 3) selectedImages.add(bitmap)
                } catch (e: Exception) { errorMessage = "Failed to load some images." }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            if (selectedImages.size < 3) { selectedImages.add(bitmap); errorMessage = null }
            else errorMessage = "Maximum 3 images allowed."
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFFF0FDF4))
    ) {
        // --- FIGMA GRADIENT HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(colors = listOf(Color(0xFF059669), Color(0xFF0D9488))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(modifier = Modifier.size(48.dp), color = Color.White.copy(alpha = 0.2f), shape = CircleShape) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White) }
                        }
                        Text("PlantDoctor AI", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload plant photos to diagnose diseases", color = Color(0xFFD1FAE5), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- UPLOAD SECTION (FIGMA UI) ---
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Upload Photos (Max 3)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = selectedImages.size < 3,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Camera")
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = selectedImages.size < 3,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }

                if (selectedImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        selectedImages.forEachIndexed { index, bitmap ->
                            Box(
                                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
                            ) {
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                IconButton(
                                    onClick = { selectedImages.removeAt(index) },
                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Red, CircleShape)
                                ) { Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- ORIGINAL API CALL TRIGGERED HERE ---
                Button(
                    onClick = {
                        if (selectedImages.isNotEmpty() && !isLoading) {
                            isLoading = true
                            errorMessage = null
                            responseText = ""
                            scope.launch {
                                try {
                                    val base64ImagesList = selectedImages.map { bitmapToBase64(it) }
                                    val result = callGeminiApi(promptText, base64ImagesList)
                                    responseText = result
                                    onAnalysisComplete?.invoke(result)
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.localizedMessage}"
                                } finally { isLoading = false }
                            }
                        } else if (selectedImages.isEmpty()) { errorMessage = "Please upload at least one image." }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = selectedImages.isNotEmpty() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669), disabledContainerColor = Color(0xFF059669).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Autorenew, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Diagnose Plant (${selectedImages.size} Images)")
                    }
                }
            }
        }

        // --- RESULT DISPLAY WRAPPED IN FIGMA CARD ---
        if (responseText.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Diagnosis Result:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectionContainer {
                            Surface(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp)) {
                                Text(text = responseText, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF374151))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { responseText = ""; selectedImages.clear() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("New Diagnosis") }
                    }
                }
            }
        } else if (responseText.isEmpty() && !isLoading) { // <--- FIXED HERE
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDEEBFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB))
                        Text(
                            text = "Tip: For best results, upload clear photos of affected leaves or parts from different angles.",
                            style = MaterialTheme.typography.bodySmall, color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

// --- HELPER UI COMPOSABLES FOR THE ABOVE SCREENS ---

@Composable
fun NutrientCard(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, style = MaterialTheme.typography.headlineSmall) },
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun SoilParameterCard(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, style = MaterialTheme.typography.headlineSmall) },
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}















data class ChatMessage(
    val id: Int,
    val text: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun AIChatbotScreen(navController: NavController) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    1,
                    "नमस्ते! मैं आपका कृषि सहायक हूं। मैं फसल, कीट, मौसम, और खेती से संबंधित किसी भी प्रश्न में आपकी मदद कर सकता हूं। आप मुझसे कुछ भी पूछ सकते हैं!",
                    "bot"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val quickQuestions = listOf(
        "गेहूं की खेती कैसे करें?",
        "टमाटर के रोग",
        "जैविक खेती के फायदे",
        "सिंचाई के तरीके",
    )

    fun sendMessage() {
        if (inputText.isBlank()) return

        val userMessageText = inputText
        val userMessage = ChatMessage(messages.size + 1, userMessageText, "user")
        messages = messages + userMessage
        inputText = ""
        isTyping = true

        scope.launch {
            listState.animateScrollToItem(messages.size)

            try {
                // --- INTEGRATED YOUR ORIGINAL GEMINI API CALL ---
                val prompt = """
                    You are an expert agricultural AI assistant. Reply strictly in Hindi language.
                    Provide a formal, short, and to-the-point reply. Do NOT use any special markdown characters like * or #.
                    User Question: $userMessageText
                """.trimIndent()

                val response = makeGeminiApiCall(GEMINI_API_KEY, prompt)

                val botMessage = ChatMessage(messages.size + 1, response, "bot")
                messages = messages + botMessage
            } catch (e: Exception) {
                val errorMsg = ChatMessage(messages.size + 1, "क्षमा करें, नेटवर्क त्रुटि हुई: ${e.localizedMessage}", "bot")
                messages = messages + errorMsg
            } finally {
                isTyping = false
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5FF))
    ) {
        // --- FIGMA HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF9333EA), Color(0xFFDB2777))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White)
                    }
                }
                Column {
                    Text(
                        text = "AI Assistant",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Always here to help",
                        color = Color(0xFFFAE8FF),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // --- CHAT MESSAGES ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }

            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        // --- QUICK QUESTIONS ---
        if (messages.size == 1) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(
                    text = "Quick questions:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickQuestions) { question ->
                        SuggestionChip(
                            onClick = { inputText = question; sendMessage() },
                            label = { Text(question, style = MaterialTheme.typography.bodySmall) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White)
                        )
                    }
                }
            }
        }

        // --- INPUT FIELD ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask me anything about farming...") },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )
            FloatingActionButton(
                onClick = { sendMessage() },
                containerColor = Color(0xFF9333EA),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.sender == "user") Arrangement.End else Arrangement.Start
    ) {
        if (message.sender == "bot") {
            Surface(
                modifier = Modifier.size(32.dp),
                color = Color(0xFF9333EA),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (message.sender == "user") Color(0xFF9333EA) else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.sender == "bot") 4.dp else 16.dp,
                bottomEnd = if (message.sender == "user") 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.sender == "user") Color.White else Color(0xFF1F2937)
            )
        }

        if (message.sender == "user") {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(32.dp),
                color = Color(0xFF4A7C59),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.size(32.dp),
            color = Color(0xFF9333EA),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        color = Color.Gray,
                        shape = CircleShape
                    ) {}
                }
            }
        }
    }
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






private suspend fun bitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Compress to JPEG, 80% quality to save bandwidth and fit API limits
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    Base64.encodeToString(byteArray, Base64.NO_WRAP)
}







private suspend fun callGeminiApi(prompt: String, base64Images: List<String>): String = withContext(Dispatchers.IO) {
    if (GEMINI_API_KEY.isEmpty()) throw Exception("API Key is missing.")

    // Note: Verify this model version. Standard is usually "gemini-1.5-flash"
    val modelName = "" +
            "" +
            "model = genAI.get_model(\"gemini-3-flash-preview\")"
    val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$GEMINI_API_KEY")

    // Construct JSON Payload
    val jsonBody = JSONObject().apply {
        put("contents", JSONArray().apply {
            put(JSONObject().apply {

                // Create the array that will hold text + all images
                val partsArray = JSONArray()

                // 1. Add Text Prompt
                partsArray.put(JSONObject().put("text", prompt))

                // 2. Loop through the list and add EACH image as a separate part
                base64Images.forEach { base64String ->
                    partsArray.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64String)
                        })
                    })
                }

                // Add the populated parts array to the main object
                put("parts", partsArray)
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

                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
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








suspend fun makeGeminiApiCall(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
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

