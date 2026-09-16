package com.example.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String = "",
    val avatarUrl: String = "",
    val avatarRes: Int? = null,
    val bio: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val likesCount: Int = 0,
    val isVerified: Boolean = false,
    val isDeveloper: Boolean = false
)

enum class WatermarkStyle(val displayName: String, val description: String) {
    NEON_CYBER("Neon Cyber", "Vibrant cyan & magenta glow with electric badge"),
    TIKTOK_CLASSIC("Tok Classic", "Clean dual-tone pill badge with music note"),
    MINIMAL_CORNER("Corner Stamp", "Subtle floating watermark with soft shadow"),
    HOLO_GLITCH("Holo Glitch", "Futuristic holographic gradient styling")
}

data class VideoPost(
    val id: String,
    val creator: User,
    val title: String,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val soundTitle: String,
    val soundAuthor: String,
    val coverImageRes: Int? = null,
    val dynamicTheme: String = "cyberpunk", // cyberpunk, dance, sunset, neon
    var likesCount: Int,
    var commentsCount: Int,
    var sharesCount: Int,
    var bookmarksCount: Int,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    var isFollowing: Boolean = false,
    val watermarkText: String = "Asifofc",
    val watermarkStyle: WatermarkStyle = WatermarkStyle.NEON_CYBER,
    val durationSeconds: Int = 15,
    val filterApplied: String = "Cyber Neon"
)

data class Comment(
    val id: String,
    val postId: String,
    val user: User,
    val text: String,
    val timestamp: String,
    var likesCount: Int = 0,
    var isLiked: Boolean = false
)

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationText: String,
    val usageCount: String,
    val category: String
)

data class VideoFilter(
    val id: String,
    val name: String,
    val description: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long
)
