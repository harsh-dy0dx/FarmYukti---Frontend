package com.example.farmyukti

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// --- DATA MODEL ---
data class Playlist(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val videos: Int,
    val duration: String,
    val instructor: String,
    val category: String,
    val youtubeUrl: String // Added URL parameter for real playback!
)

// --- MAIN SCREEN ---
@Composable
fun LearningResourcesScreen() {
    val context = LocalContext.current

    // Put your actual YouTube Playlist URLs here!
    val playlists = listOf(
        Playlist(
            id = 1,
            title = "Modern Wheat Farming Techniques",
            thumbnail = "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=400",
            videos = 24,
            duration = "3h 45m",
            instructor = "",
            category = "Crops",
            youtubeUrl = "https://www.youtube.com/watch?v=dGXd7ZDWDr4&list=PLSGhxGtqUyKDSyoPKOial45zH71OeKsI9" // Replace with your actual link
        ),
        Playlist(
            id = 2,
            title = "Organic Farming Complete Guide",
            thumbnail = "https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=400",
            videos = 54,
            duration = "5h 30m",
            instructor = "",
            category = "Organic",
            youtubeUrl = "https://www.youtube.com/watch?v=sD6ysm5_nIM&list=PLrGOiMdeyxH0YPnTUmVd4qtuODdmkwmd-" // Replace with your actual link
        ),
        Playlist(
            id = 3,
            title = "Pest Management & Control",
            thumbnail = "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400",
            videos = 1,
            duration = "5m",
            instructor = "",
            category = "Disease",
            youtubeUrl = "https://youtu.be/CELaZ62mkfY" // Replace with your actual link
        )
    )

    val categories = listOf("All", "Crops", "Organic", "Disease", "Technology", "Vegetables", "Soil")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEF2FF)) // Light blue background from Figma
    ) {
        // --- FIGMA GRADIENT HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(colors = listOf(Color(0xFF6366F1), Color(0xFF9333EA))), // Purple gradient
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
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                        }
                    }
                    Column {
                        Text("Guided Learning", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Expert video tutorials & farming guides", color = Color(0xFFE0E7FF), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SCROLLABLE CATEGORY CHIPS ---
        item {
            LazyRow(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == "All", // You can add state logic here later if you want to filter
                        onClick = { },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.Gray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = category == "All",
                            borderColor = Color.Transparent
                        )
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- YOUTUBE PLAYLIST CARDS ---
        items(playlists) { playlist ->
            PlaylistCard(playlist = playlist, context = context)
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

// --- INDIVIDUAL PLAYLIST CARD ---
@Composable
fun PlaylistCard(playlist: Playlist, context: android.content.Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Thumbnail with Play Button Overlay
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = playlist.thumbnail,
                    contentDescription = playlist.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                )

                // Category Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = playlist.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }

                // Play Button Icon
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = CircleShape
                ) {
                    Box(modifier = Modifier.size(64.dp).padding(12.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(40.dp))
                    }
                }
            }

            // Playlist Details & Button
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = playlist.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "By ${playlist.instructor}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF6B7280))
                        Text(text = "${playlist.videos} videos", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF6B7280))
                        Text(text = playlist.duration, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ACTUAL YOUTUBE INTENT LAUNCHER
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playlist.youtubeUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open video link.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Playlist", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}