//package com.example.farmyukti
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.collectIsDraggedAsState
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//public var DataModelist : List<BannerModel> = mutableListOf(BannerModel("1","image 1",
//    "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152289/PradhanMantriKisanSammanNidhiBanner_ccxxzg.jpg",
//    Color.White),
//    BannerModel("1","image 1",
//        "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152289/PradhanMantriKisanSammanNidhiInstallment_onlgcv.jpg",
//        Color.White),
//    BannerModel("1","image 1",
//        "https://res.cloudinary.com/dhrqr1wiv/image/upload/v1764152285/15-Government-Schemes-for-Farmers-in-India_iq3r8q.webp",
//        Color.White));
//
//// 1. Data Model
//data class BannerModel(
//    val id: String,
//    val title: String,
//    val imageUrl: String, // In a real app, this would be a URL
//    val backgroundColor: Color // Used for this demo instead of real network images
//)
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun AutoSlidingBanner(
//    banners: List<BannerModel>,
//    modifier: Modifier = Modifier,
//    onBannerClick: (BannerModel) -> Unit
//) {
//    // 2. Setup Pager State
//    val pagerState = rememberPagerState(pageCount = { banners.size })
//
//    // Check if user is currently dragging the banner to pause auto-scroll
//    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
//
//    // 3. Auto-Scroll Logic
//    LaunchedEffect(key1 = pagerState.currentPage, key2 = isDragged) {
//        // Only auto-scroll if the list is not empty and user isn't holding it
//        if (banners.isNotEmpty() && !isDragged) {
//            delay(10000) // Wait for 1 second
//
//            // Calculate next page (loop back to 0 if at end)
//            val nextPage = (pagerState.currentPage + 1) % banners.size
//
//            // Animate to next page
//            pagerState.animateScrollToPage(nextPage)
//        }
//    }
//
//    // 4. UI Layout
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp) // Set your desired banner height
//        ) {
//            HorizontalPager(
//                state = pagerState,
//                contentPadding = PaddingValues(horizontal = 16.dp), // Peek next items
//                pageSpacing = 8.dp,
//                modifier = Modifier.fillMaxSize()
//            ) { page ->
//                val banner = banners[page]
//
//                BannerItem(
//                    banner = banner,
//                    onClick = { onBannerClick(banner) }
//                )
//            }
//
//            // Indicators (Dots)
//            Row(
//                Modifier
//                    .height(50.dp)
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 8.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                repeat(banners.size) { iteration ->
//                    val color = if (pagerState.currentPage == iteration)
//                        Color.White else Color.White.copy(alpha = 1f)
//
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .clip(CircleShape)
//                            .background(color)
//                            .size(8.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BannerItem(
//    banner: BannerModel,
//    onClick: () -> Unit
//) {
//    Card(
//        shape =RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
//        modifier = Modifier
//            .fillMaxSize()
//            .clickable { onClick() }
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//
//            // --- IMAGE PLACEHOLDER ---
//            // In a real app, replace this Box with AsyncImage (Coil)
//
//            AsyncImage(
//                model = banner.imageUrl,
//                contentDescription = banner.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//
//
//            // Mock Image (Colored Box)
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .background(Color.White)
////            )
////
////            // Text Overlay
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .background(Color.White
//////                        Brush.verticalGradient(
//////                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
//////                            startY = 100f
//////                        )
////                    ),
////                contentAlignment = Alignment.BottomStart
////            ) {
////                Text(
////                    text = banner.title,
////                    color = Color.White,
////                    fontSize = 18.sp,
////                    fontWeight = FontWeight.Bold,
////                    modifier = Modifier.padding(16.dp)
////                )
////            }
//        }
//    }
//}
//
//// 5. Example Usage / Preview
//@Preview(showBackground = true)
//@Composable
//fun DashboardPreview() {
////    val sampleBanners = listOf(
////        BannerModel("1", "Big Summer Sale", "", Color(0xFFEF5350)), // Red
////        BannerModel("2", "New Arrivals", "", Color(0xFF42A5F5)),    // Blue
////        BannerModel("3", "Limited Time Offer", "", Color(0xFF66BB6A)), // Green
////        BannerModel("4", "Flash Deal", "", Color(0xFFFFA726))       // Orange
////    )
//
//    MaterialTheme {
//        Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
//            Text(
//                "My Dashboard",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.padding(16.dp)
//            )
//
//            AutoSlidingBanner(
//                banners = DataModelist ,
//                onBannerClick = { clickedBanner ->
//                    println("Clicked on: ${clickedBanner.title}")
//                }
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Other dashboard content...
//            Text(
//                "Other content goes here...",
//                modifier = Modifier.padding(16.dp),
//                color = Color.Gray
//            )
//        }
//    }
//}