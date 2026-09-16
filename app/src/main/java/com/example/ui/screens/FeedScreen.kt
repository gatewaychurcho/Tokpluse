package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.Comment
import com.example.model.User
import com.example.model.VideoPost
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokPink
import com.example.ui.theme.TokSurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    posts: List<VideoPost>,
    currentUser: User?,
    onOpenSearch: () -> Unit,
    onOpenDeveloperContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedFeedTab by remember { mutableIntStateOf(1) } // 0: Following, 1: For You
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { posts.size })

    // Active playback state
    var isPlaying by remember { mutableStateOf(true) }

    // Bottom sheets state
    var activeCommentPostId by remember { mutableStateOf<String?>(null) }
    var activeSharePost by remember { mutableStateOf<VideoPost?>(null) }

    // Disk spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "disk_rotation")
    val diskAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disk"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Vertical Pager for TikTok swipe feed
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val post = posts[page]
            var isLiked by remember(post.id) { mutableStateOf(post.isLiked) }
            var likesCount by remember(post.id) { mutableIntStateOf(post.likesCount) }
            var isBookmarked by remember(post.id) { mutableStateOf(post.isBookmarked) }
            var bookmarksCount by remember(post.id) { mutableIntStateOf(post.bookmarksCount) }
            var isFollowing by remember(post.id) { mutableStateOf(post.isFollowing) }

            Box(modifier = Modifier.fillMaxSize()) {
                // Background Video Player View
                VideoPlayerView(
                    post = post,
                    isPlaying = isPlaying && pagerState.currentPage == page,
                    onTogglePlay = { isPlaying = !isPlaying },
                    onDoubleTapLike = {
                        if (!isLiked) {
                            isLiked = true
                            likesCount += 1
                            post.isLiked = true
                            post.likesCount = likesCount
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Right Action Sidebar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 80.dp)
                ) {
                    // Creator Avatar + Follow (+) Badge
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(TokSurfaceVariant)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = post.creator.name.take(1).uppercase(),
                                color = TokCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        // Follow Toggle Button
                        if (!isFollowing) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(TokPink)
                                    .clickable {
                                        isFollowing = true
                                        post.isFollowing = true
                                    }
                                    .testTag("follow_creator_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Follow",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Like Button
                    FeedActionButton(
                        icon = Icons.Default.Favorite,
                        count = formatNumber(likesCount),
                        tint = if (isLiked) TokPink else Color.White,
                        testTag = "feed_like_button",
                        onClick = {
                            isLiked = !isLiked
                            likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                            post.isLiked = isLiked
                            post.likesCount = likesCount
                        }
                    )

                    // Comment Button
                    FeedActionButton(
                        icon = Icons.Default.Comment,
                        count = formatNumber(post.commentsCount),
                        tint = Color.White,
                        testTag = "feed_comment_button",
                        onClick = {
                            activeCommentPostId = post.id
                        }
                    )

                    // Bookmark / Favorite Button
                    FeedActionButton(
                        icon = Icons.Default.Bookmark,
                        count = formatNumber(bookmarksCount),
                        tint = if (isBookmarked) Color(0xFFFFB300) else Color.White,
                        testTag = "feed_bookmark_button",
                        onClick = {
                            isBookmarked = !isBookmarked
                            bookmarksCount = if (isBookmarked) bookmarksCount + 1 else bookmarksCount - 1
                            post.isBookmarked = isBookmarked
                            post.bookmarksCount = bookmarksCount
                        }
                    )

                    // Share Button
                    FeedActionButton(
                        icon = Icons.Default.Share,
                        count = formatNumber(post.sharesCount),
                        tint = Color.White,
                        testTag = "feed_share_button",
                        onClick = {
                            activeSharePost = post
                        }
                    )

                    // Rotating Music Vinyl Disk
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E28))
                            .border(6.dp, Color(0xFF121218), CircleShape)
                            .rotate(if (isPlaying) diskAngle else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(TokPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                // Bottom Left Video Details (Creator, Caption, Music)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .padding(start = 16.dp, bottom = 78.dp)
                ) {
                    // Creator Handle & Verified Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "@${post.creator.username}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (post.creator.isVerified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Creator",
                                tint = TokCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (post.creator.isDeveloper) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TokPink)
                                    .clickable { onOpenDeveloperContact() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DEV",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Caption
                    Text(
                        text = post.caption,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Hashtags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        post.hashtags.take(3).forEach { tag ->
                            Text(
                                text = "#$tag",
                                color = TokCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sound Track Ticker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x55000000))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${post.soundTitle} - ${post.soundAuthor}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Top App Bar: Tabs ("Following" | "For You") + Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live badge or Developer Shortcut
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x66000000))
                    .clickable { onOpenDeveloperContact() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "DEV INFO",
                    color = TokCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Feed Switching Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Following",
                    color = if (selectedFeedTab == 0) Color.White else Color(0x88FFFFFF),
                    fontSize = if (selectedFeedTab == 0) 17.sp else 15.sp,
                    fontWeight = if (selectedFeedTab == 0) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .clickable { selectedFeedTab = 0 }
                        .padding(4.dp)
                )

                Text(
                    text = "For You",
                    color = if (selectedFeedTab == 1) Color.White else Color(0x88FFFFFF),
                    fontSize = if (selectedFeedTab == 1) 17.sp else 15.sp,
                    fontWeight = if (selectedFeedTab == 1) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .clickable { selectedFeedTab = 1 }
                        .padding(4.dp)
                )
            }

            // Search Icon
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier.testTag("feed_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Active Comments Bottom Sheet
        if (activeCommentPostId != null) {
            val commentsList = SampleData.sampleComments[activeCommentPostId] ?: emptyList()
            CommentBottomSheet(
                postId = activeCommentPostId!!,
                initialComments = commentsList,
                currentUser = currentUser,
                onDismiss = { activeCommentPostId = null }
            )
        }

        // Active Share Bottom Sheet
        if (activeSharePost != null) {
            ShareBottomSheet(
                post = activeSharePost!!,
                onDismiss = { activeSharePost = null }
            )
        }
    }
}

@Composable
private fun FeedActionButton(
    icon: ImageVector,
    count: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x33000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = count,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black,
                    blurRadius = 4f
                )
            )
        )
    }
}

private fun formatNumber(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
