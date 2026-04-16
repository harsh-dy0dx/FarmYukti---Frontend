package com.example.farmyukti

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.farmyukti.repo.safeClickable
import kotlinx.coroutines.delay

var DataModelist : List<BannerModel> = mutableListOf(
    BannerModel("1","image 1", "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152289/PradhanMantriKisanSammanNidhiBanner_ccxxzg.jpg", Color.White),
    BannerModel("2","image 2", "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152289/PradhanMantriKisanSammanNidhiInstallment_onlgcv.jpg", Color.White),
    BannerModel("3","image 3", "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152285/15-Government-Schemes-for-Farmers-in-India_iq3r8q.webp", Color.White)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingBanner(banners: List<BannerModel>, modifier: Modifier = Modifier, onBannerClick: (BannerModel) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(key1 = pagerState.currentPage, key2 = isDragged) {
        if (banners.isNotEmpty() && !isDragged) {
            delay(10000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            HorizontalPager(state = pagerState, contentPadding = PaddingValues(horizontal = 16.dp), pageSpacing = 8.dp, modifier = Modifier.fillMaxSize()) { page ->
                val banner = banners[page]
                BannerItem(banner = banner, onClick = { onBannerClick(banner) })
            }
            Row(Modifier.height(50.dp).fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
                repeat(banners.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 1f)
                    Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
                }
            }
        }
    }
}

@Composable
fun BannerItem(banner: BannerModel, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxSize().safeClickable{ onClick() }) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = banner.imageUrl, contentDescription = banner.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(modifier = Modifier.safeClickable { onClick() }.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
fun CategoryChip(category: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Text(text = category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun AdvisoryCard(advisory: Advisory) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = when(advisory.type) {
                AdvisoryType.WEATHER -> Icons.Default.WaterDrop
                AdvisoryType.PEST -> Icons.Default.PestControl
                AdvisoryType.FERTILIZER -> Icons.Default.Eco
                else -> Icons.Default.Warning
            }, contentDescription = "Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = advisory.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = advisory.summary, style = MaterialTheme.typography.bodyMedium)
                Text(text = advisory.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ProduceListItem(
    listing: ProduceListing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showChatButton: Boolean = false,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .safeClickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image
                val imageToLoad = if (listing.imageUrls.isNotEmpty()) listing.imageUrls.first() else listing.imageUrl
                if (imageToLoad.isNotEmpty()) {
                    AsyncImage(
                        model = imageToLoad,
                        contentDescription = listing.produceName,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Image, null, tint = Color.White) }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = listing.produceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Grade Pill
                        Surface(
                            color = if (listing.aiQualityGrade.contains("A")) Color(0xFFDCFCE7) else Color(0xFFFEF9C3),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = listing.aiQualityGrade,
                                color = if (listing.aiQualityGrade.contains("A")) Color(0xFF15803D) else Color(0xFF854D0E),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Sold by: ${listing.farmerName}", style = MaterialTheme.typography.bodySmall, color = MutedForeground)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Qty: ${listing.quantityKg} kg", style = MaterialTheme.typography.bodySmall, color = MutedForeground)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${listing.basePricePerKg}/kg",
                            style = MaterialTheme.typography.titleMedium,
                            color = FarmPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = MutedForeground)
                            Text(text = listing.location, style = MaterialTheme.typography.bodySmall, color = MutedForeground)
                        }
                    }
                }
            }

            // Buttons
            if (showChatButton || onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (showChatButton) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("https://api.whatsapp.com/send?phone=${listing.contactNumber}")
                                try { context.startActivity(intent) } catch (e: Exception) { Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show() }
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Chat", fontSize = 14.sp)
                        }
                    }
                    if (onDelete != null) {
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.height(40.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = BorderStroke(1.dp, Color.Red),
                            shape = RoundedCornerShape(12.dp)
                        ) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp)) }
                    }
                }
            }
        }
    }
}