package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthManager
import com.example.data.SampleData
import com.example.model.VideoPost
import com.example.ui.screens.ContactDeveloperScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.GoogleAuthScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokDarkBg
import com.example.ui.theme.TokPink
import com.example.ui.theme.TokSurface

enum class AppDestination {
    SPLASH, AUTH, MAIN, EDITOR, CONTACT_DEV
}

@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val authState by authManager.authState.collectAsState()

    var currentDestination by remember { mutableStateOf(AppDestination.SPLASH) }
    var selectedBottomTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Discover, 2: Inbox, 3: Profile

    // Dynamic posts feed state
    val posts = remember { mutableStateListOf(*SampleData.initialPosts.toTypedArray()) }

    // System back handler
    BackHandler(enabled = currentDestination != AppDestination.MAIN && currentDestination != AppDestination.SPLASH) {
        currentDestination = AppDestination.MAIN
    }

    Crossfade(targetState = currentDestination, label = "main_navigation") { destination ->
        when (destination) {
            AppDestination.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentDestination = if (authState.isAuthenticated) {
                            AppDestination.MAIN
                        } else {
                            AppDestination.AUTH
                        }
                    }
                )
            }

            AppDestination.AUTH -> {
                GoogleAuthScreen(
                    authManager = authManager,
                    onAuthSuccess = { currentDestination = AppDestination.MAIN },
                    onSkipToFeed = { currentDestination = AppDestination.MAIN }
                )
            }

            AppDestination.EDITOR -> {
                VideoEditorScreen(
                    currentUser = authState.currentUser,
                    onPublishVideo = { newPost ->
                        posts.add(0, newPost) // Add to top of feed
                        currentDestination = AppDestination.MAIN
                        selectedBottomTab = 0 // Return to feed
                    },
                    onClose = { currentDestination = AppDestination.MAIN }
                )
            }

            AppDestination.CONTACT_DEV -> {
                ContactDeveloperScreen(
                    currentUser = authState.currentUser,
                    onBack = { currentDestination = AppDestination.MAIN }
                )
            }

            AppDestination.MAIN -> {
                Scaffold(
                    bottomBar = {
                        TokBottomNavigationBar(
                            selectedTab = selectedBottomTab,
                            onTabSelected = { tabIndex ->
                                if (tabIndex == 2) {
                                    // Open Create / Video Editor Studio
                                    currentDestination = AppDestination.EDITOR
                                } else {
                                    selectedBottomTab = tabIndex
                                }
                            }
                        )
                    },
                    modifier = modifier.fillMaxSize(),
                    containerColor = TokDarkBg
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (selectedBottomTab == 0) 0.dp else innerPadding.calculateBottomPadding())
                    ) {
                        when (selectedBottomTab) {
                            0 -> {
                                FeedScreen(
                                    posts = posts,
                                    currentUser = authState.currentUser,
                                    onOpenSearch = { selectedBottomTab = 1 },
                                    onOpenDeveloperContact = { currentDestination = AppDestination.CONTACT_DEV }
                                )
                            }
                            1 -> {
                                DiscoverScreen(
                                    onSelectCreator = { currentDestination = AppDestination.CONTACT_DEV }
                                )
                            }
                            3 -> {
                                InboxScreen()
                            }
                            4 -> {
                                ProfileScreen(
                                    user = authState.currentUser,
                                    authState = authState,
                                    posts = posts,
                                    onOpenContactDeveloper = { currentDestination = AppDestination.CONTACT_DEV },
                                    onSignOut = {
                                        authManager.signOut()
                                        currentDestination = AppDestination.AUTH
                                    },
                                    onOpenAuth = { currentDestination = AppDestination.AUTH }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TokBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.95f))
            .navigationBarsPadding()
            .height(56.dp)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home / Feed
            TokNavTab(
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                isSelected = selectedTab == 0,
                testTag = "nav_tab_home",
                onClick = { onTabSelected(0) }
            )

            // Discover
            TokNavTab(
                label = "Discover",
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                isSelected = selectedTab == 1,
                testTag = "nav_tab_discover",
                onClick = { onTabSelected(1) }
            )

            // Center Create (+) Button with iconic TikTok Cyan/Pink border
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onTabSelected(2) }
                    .testTag("nav_tab_create"),
                contentAlignment = Alignment.Center
            ) {
                // Background dual layer (Cyan left, Pink right)
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxSize().background(TokCyan))
                    Box(modifier = Modifier.weight(1f).fillMaxSize().background(TokPink))
                }
                // White inner badge
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Video",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Inbox
            TokNavTab(
                label = "Inbox",
                selectedIcon = Icons.Filled.ChatBubbleOutline,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                isSelected = selectedTab == 3,
                testTag = "nav_tab_inbox",
                onClick = { onTabSelected(3) }
            )

            // Profile
            TokNavTab(
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                isSelected = selectedTab == 4,
                testTag = "nav_tab_profile",
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
private fun TokNavTab(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF7A7B8A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Color(0xFF7A7B8A),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
