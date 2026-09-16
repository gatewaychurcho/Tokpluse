package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthManager
import com.example.data.AuthState
import com.example.model.User
import com.example.model.VideoPost
import com.example.ui.components.WatermarkView
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokDarkBg
import com.example.ui.theme.TokPink
import com.example.ui.theme.TokSurface
import com.example.ui.theme.TokSurfaceVariant

@Composable
fun ProfileScreen(
    user: User?,
    authState: AuthState,
    posts: List<VideoPost>,
    onOpenContactDeveloper: () -> Unit,
    onSignOut: () -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Videos, 1: Liked, 2: Bookmarks

    val userPosts = remember(posts, user) {
        posts.filter { it.creator.id == user?.id || it.creator.id == "dev_asifofc" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TokDarkBg)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user?.name ?: "Profile",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                if (user?.isVerified == true) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = TokCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Quick Contact Dev Button
                IconButton(
                    onClick = onOpenContactDeveloper,
                    modifier = Modifier.testTag("profile_contact_developer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContactSupport,
                        contentDescription = "Contact Developer",
                        tint = TokCyan
                    )
                }

                if (authState.isAuthenticated) {
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("profile_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFA0A2B4)
                        )
                    }
                }
            }
        }

        // Profile Avatar & Handle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(TokSurfaceVariant)
                        .border(2.dp, Brush.linearGradient(listOf(TokCyan, TokPink)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.name ?: "U").take(1).uppercase(),
                        color = TokCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "@${user?.username ?: "guest"}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Google Verified Auth Pill
            if (authState.isAuthenticated && authState.authMethod == "Google Sign-In") {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2225F4EE))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TokCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = user?.email ?: "Google Account",
                        color = TokCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (!authState.isAuthenticated) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onOpenAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = TokPink),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(text = "Sign in with Google", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Row (Following, Followers, Likes)
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatColumn(count = "${user?.followingCount ?: 48}", label = "Following")
                ProfileStatColumn(count = "${user?.followersCount ?: 1240}", label = "Followers")
                ProfileStatColumn(count = "${user?.likesCount ?: 18200}", label = "Likes")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Button(
                    onClick = onOpenContactDeveloper,
                    colors = ButtonDefaults.buttonColors(containerColor = TokSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.ContactSupport, contentDescription = null, tint = TokCyan, modifier = Modifier.size(16.dp))
                        Text(text = "Contact Dev", color = Color.White, fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = { /* Edit profile details */ },
                    colors = ButtonDefaults.buttonColors(containerColor = TokSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(text = "Edit Profile", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Signature Watermark Promo Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TokSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Active Video Watermark",
                            color = Color(0xFFA0A2B4),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Powered by Asifofc Engine",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    WatermarkView(text = "Asifofc")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs (Videos, Liked, Bookmarks)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = TokDarkBg,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = TokCyan,
                    height = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Default.GridOn, contentDescription = "Videos", tint = if (selectedTab == 0) TokCyan else Color(0xFF70717D)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Liked", tint = if (selectedTab == 1) TokCyan else Color(0xFF70717D)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks", tint = if (selectedTab == 2) TokCyan else Color(0xFF70717D)) }
            )
        }

        // Videos Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            items(userPosts) { post ->
                Box(
                    modifier = Modifier
                        .aspectRatio(0.8f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(TokSurfaceVariant),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF201636), Color(0xFF100C1C))
                                )
                            )
                    )

                    // Video Play & Likes count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${post.likesCount}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFFA0A2B4),
            fontSize = 12.sp
        )
    }
}
