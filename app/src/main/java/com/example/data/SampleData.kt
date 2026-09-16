package com.example.data

import com.example.R
import com.example.model.Comment
import com.example.model.SoundTrack
import com.example.model.User
import com.example.model.VideoFilter
import com.example.model.VideoPost
import com.example.model.WatermarkStyle

object SampleData {

    val developerUser = User(
        id = "dev_asifofc",
        name = "Asif",
        username = "asifofc",
        email = "asifofc.dev@gmail.com",
        bio = "Official Creator & Developer of TokPulse ⚡ Crafting ultra-smooth mobile feeds & video editing tools. DM for collaborations!",
        followersCount = 284500,
        followingCount = 124,
        likesCount = 3890000,
        isVerified = true,
        isDeveloper = true
    )

    val users = listOf(
        developerUser,
        User(
            id = "user_maya",
            name = "Maya Lin",
            username = "mayavibes",
            bio = "Visual storyteller & neon aesthetics ✨ Tokyo / NYC",
            followersCount = 94200,
            followingCount = 320,
            likesCount = 1450000,
            isVerified = true
        ),
        User(
            id = "user_leo",
            name = "Leo Beatmaker",
            username = "leoproduces",
            bio = "Dropping new drops daily 🎧 Use my audio!",
            followersCount = 61800,
            followingCount = 180,
            likesCount = 780000,
            isVerified = false
        ),
        User(
            id = "user_zara",
            name = "Zara Dance",
            username = "zarachoreo",
            bio = "Street style freestyle dancer 💃 Join the trend #TokDance",
            followersCount = 152000,
            followingCount = 210,
            likesCount = 2900000,
            isVerified = true
        )
    )

    val initialPosts = listOf(
        VideoPost(
            id = "post_1",
            creator = developerUser,
            title = "TokPulse Video Engine ⚡",
            caption = "Welcome to TokPulse! Ultra responsive short-form video editing, real-time filters & stylish watermark Asifofc built right in 🚀 Let me know your thoughts in comments!",
            hashtags = listOf("TokPulse", "Asifofc", "VideoEditor", "CreativeCode", "Viral"),
            soundTitle = "TokPulse Bass Drive - Asifofc Original",
            soundAuthor = "asifofc",
            coverImageRes = R.drawable.tok_clip_cyberpunk,
            dynamicTheme = "cyberpunk",
            likesCount = 148200,
            commentsCount = 2390,
            sharesCount = 18400,
            bookmarksCount = 31200,
            isLiked = false,
            isBookmarked = false,
            isFollowing = true,
            watermarkText = "Asifofc",
            watermarkStyle = WatermarkStyle.NEON_CYBER,
            durationSeconds = 15,
            filterApplied = "Cyber Neon"
        ),
        VideoPost(
            id = "post_2",
            creator = users[3], // Zara Dance
            title = "Stage Spotlight Freestyle",
            caption = "Testing out the new VHS Vintage filter on TokPulse 🔥 The color grading feels incredible! Audio by @leoproduces",
            hashtags = listOf("TokDance", "Freestyle", "DanceTrend", "Asifofc", "FYP"),
            soundTitle = "Midnight Groove (Extended Mix)",
            soundAuthor = "leoproduces",
            coverImageRes = R.drawable.tok_clip_dance,
            dynamicTheme = "dance",
            likesCount = 89400,
            commentsCount = 1120,
            sharesCount = 6700,
            bookmarksCount = 14800,
            isLiked = true,
            isBookmarked = true,
            isFollowing = false,
            watermarkText = "Asifofc",
            watermarkStyle = WatermarkStyle.TIKTOK_CLASSIC,
            durationSeconds = 22,
            filterApplied = "Vintage VHS"
        ),
        VideoPost(
            id = "post_3",
            creator = users[1], // Maya Lin
            title = "Golden Hour In Neo Tokyo",
            caption = "Sunsets hitting different with the Golden Hour preset 🌆 Edited fully in the TokPulse studio. Watermark badge looks clean!",
            hashtags = listOf("GoldenHour", "TokyoNights", "Cinematic", "Aesthetic"),
            soundTitle = "Pastel Dreams Lo-Fi",
            soundAuthor = "mayavibes",
            coverImageRes = null,
            dynamicTheme = "sunset",
            likesCount = 63200,
            commentsCount = 740,
            sharesCount = 4200,
            bookmarksCount = 9800,
            isLiked = false,
            isBookmarked = false,
            isFollowing = false,
            watermarkText = "Asifofc",
            watermarkStyle = WatermarkStyle.HOLO_GLITCH,
            durationSeconds = 18,
            filterApplied = "Golden Hour"
        )
    )

    val filters = listOf(
        VideoFilter(
            id = "f_normal",
            name = "Original",
            description = "Natural raw camera colors with crisp dynamic range",
            primaryColorHex = 0xFFFFFFFF,
            secondaryColorHex = 0xFF888888
        ),
        VideoFilter(
            id = "f_cyber",
            name = "Cyber Neon",
            description = "Electric cyan & hot pink tones with enhanced glow",
            primaryColorHex = 0xFF25F4EE,
            secondaryColorHex = 0xFFFE2C55
        ),
        VideoFilter(
            id = "f_vhs",
            name = "Vintage VHS",
            description = "90s retro tape warm grain and chromatic scan lines",
            primaryColorHex = 0xFFFFB74D,
            secondaryColorHex = 0xFFE91E63
        ),
        VideoFilter(
            id = "f_golden",
            name = "Golden Hour",
            description = "Sun-drenched amber glow with softened warm highlights",
            primaryColorHex = 0xFFFFB300,
            secondaryColorHex = 0xFFFF7043
        ),
        VideoFilter(
            id = "f_noir",
            name = "Noir B&W",
            description = "High-contrast monochrome cinema mood",
            primaryColorHex = 0xFFE0E0E0,
            secondaryColorHex = 0xFF101010
        ),
        VideoFilter(
            id = "f_pastel",
            name = "Pastel Dream",
            description = "Soft lavender & dreamy cotton candy tint",
            primaryColorHex = 0xFFCE93D8,
            secondaryColorHex = 0xFF80DEEA
        ),
        VideoFilter(
            id = "f_glitch",
            name = "Holo Glitch",
            description = "Futuristic RGB channel displacement and holographic aura",
            primaryColorHex = 0xFF00E676,
            secondaryColorHex = 0xFFD500F9
        )
    )

    val soundTracks = listOf(
        SoundTrack("s_1", "TokPulse Bass Drive", "asifofc", "0:45", "1.2M videos", "Trending"),
        SoundTrack("s_2", "Midnight Groove Mix", "Leo Beatmaker", "0:30", "840K videos", "Dance"),
        SoundTrack("s_3", "Pastel Dreams Lo-Fi", "Maya Lin", "0:60", "520K videos", "Chill"),
        SoundTrack("s_4", "HyperPop Tokyo Surge", "NeoSound", "0:15", "390K videos", "Electronic"),
        SoundTrack("s_5", "Urban Night Walk", "SynthWave Records", "0:35", "210K videos", "Aesthetic")
    )

    val sampleComments = mapOf(
        "post_1" to listOf(
            Comment("c_1", "post_1", users[1], "The stylish Asifofc watermark is so clean! Love the neon glow 🔥", "2m ago", 182, true),
            Comment("c_2", "post_1", users[2], "Editing filters are super responsive! Best TikTok alternative UI 👏", "15m ago", 89, false),
            Comment("c_3", "post_1", users[3], "Asif killed it with this build! Huge respect to the developer 🚀", "1h ago", 342, true),
            Comment("c_4", "post_1", User("u_test", "Alex", "alex_tok", bio = ""), "Can we customize watermark position in editor?", "3h ago", 24, false)
        ),
        "post_2" to listOf(
            Comment("c_5", "post_2", developerUser, "Awesome choreo! The Vintage VHS filter looks so smooth on your dance moves 💃", "20m ago", 230, true),
            Comment("c_6", "post_2", users[1], "Need this tutorial ASAP!", "45m ago", 67, false)
        ),
        "post_3" to listOf(
            Comment("c_7", "post_3", developerUser, "The golden hour gradient came out pristine! ✨", "10m ago", 95, true)
        )
    )
}
