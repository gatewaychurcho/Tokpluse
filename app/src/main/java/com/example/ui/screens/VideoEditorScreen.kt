package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.SampleData
import com.example.model.SoundTrack
import com.example.model.User
import com.example.model.VideoFilter
import com.example.model.VideoPost
import com.example.model.WatermarkStyle
import com.example.ui.components.WatermarkView
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokDarkBg
import com.example.ui.theme.TokPink
import com.example.ui.theme.TokSurface
import com.example.ui.theme.TokSurfaceVariant
import kotlinx.coroutines.delay
import kotlin.math.sin

enum class EditorToolTab {
    NONE, FILTERS, SPEED, TEXT, SOUNDS, WATERMARK, PUBLISH
}

@Composable
fun VideoEditorScreen(
    currentUser: User?,
    onPublishVideo: (VideoPost) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Studio State
    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var activeTool by remember { mutableStateOf(EditorToolTab.NONE) }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var selectedFilter by remember { mutableStateOf(SampleData.filters[1]) } // Cyber Neon by default
    var filterIntensity by remember { mutableFloatStateOf(0.85f) }
    var selectedSound by remember { mutableStateOf(SampleData.soundTracks.first()) }
    var selectedWatermarkStyle by remember { mutableStateOf(WatermarkStyle.NEON_CYBER) }
    var watermarkPosition by remember { mutableStateOf(Alignment.TopStart) }
    var customOverlayText by remember { mutableStateOf("") }
    var overlayTextColor by remember { mutableStateOf(Color.White) }
    var pickedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var captionText by remember { mutableStateOf("") }

    // Media Picker for Gallery videos/photos
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedMediaUri = uri
            activeTool = EditorToolTab.FILTERS
        }
    }

    // Timer countdown recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingProgress = 0f
            while (isRecording && recordingProgress < 1f) {
                delay(100)
                recordingProgress += 0.015f
            }
            if (recordingProgress >= 1f) {
                isRecording = false
                activeTool = EditorToolTab.PUBLISH
            }
        }
    }

    // Dynamic wave animation for camera studio
    val infiniteTransition = rememberInfiniteTransition(label = "studio_canvas")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    // ColorMatrix for Live Filter preview
    val colorMatrix = remember(selectedFilter, filterIntensity) {
        when (selectedFilter.name) {
            "Cyber Neon" -> ColorMatrix().apply {
                setToSaturation(1f + (0.6f * filterIntensity))
            }
            "Vintage VHS" -> ColorMatrix(
                floatArrayOf(
                    1f + (0.2f * filterIntensity), 0f, 0f, 0f, 20f * filterIntensity,
                    0f, 0.95f, 0f, 0f, 10f * filterIntensity,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Golden Hour" -> ColorMatrix(
                floatArrayOf(
                    1f + (0.3f * filterIntensity), 0f, 0f, 0f, 30f * filterIntensity,
                    0f, 1f + (0.1f * filterIntensity), 0f, 0f, 15f * filterIntensity,
                    0f, 0f, 0.75f, 0f, -15f * filterIntensity,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Noir B&W" -> ColorMatrix().apply {
                setToSaturation(1f - filterIntensity)
            }
            "Pastel Dream" -> ColorMatrix().apply {
                setToSaturation(0.7f)
            }
            else -> ColorMatrix()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Preview Viewport
        Box(modifier = Modifier.fillMaxSize()) {
            if (pickedMediaUri != null) {
                AsyncImage(
                    model = pickedMediaUri,
                    contentDescription = "Imported Media",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Interactive dynamic studio recording canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF140D26),
                                Color(0xFF281136),
                                Color(0xFF0F0818)
                            )
                        )
                    )

                    // Visual studio grid and lens guidelines
                    val gridStep = w / 6f
                    for (i in 1..5) {
                        drawLine(
                            color = Color(0x1AFFFFFF),
                            start = Offset(i * gridStep, 0f),
                            end = Offset(i * gridStep, h),
                            strokeWidth = 1f
                        )
                    }

                    // Simulated live camera sensor particles
                    for (p in 0..8) {
                        val cx = w * 0.5f + (sin(Math.toRadians((waveOffset + p * 40).toDouble())).toFloat() * 120f)
                        val cy = h * 0.45f + (sin(Math.toRadians((waveOffset * 1.3 + p * 30).toDouble())).toFloat() * 140f)
                        drawCircle(
                            color = if (p % 2 == 0) TokCyan.copy(alpha = 0.25f) else TokPink.copy(alpha = 0.25f),
                            radius = (15 + p * 4).toFloat(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            // Stylized Asifofc Watermark Preview overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                WatermarkView(
                    text = "Asifofc",
                    style = selectedWatermarkStyle,
                    modifier = Modifier.align(watermarkPosition)
                )
            }

            // Custom Overlay Text Sticker
            if (customOverlayText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, TokCyan, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = customOverlayText,
                        color = overlayTextColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Active Recording Progress bar at top
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0x44FFFFFF))
                        .align(Alignment.TopCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(recordingProgress)
                            .height(4.dp)
                            .background(TokPink)
                    )
                }
            }
        }

        // Top Controls (Close, Sounds, Flash)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x55000000))
                    .testTag("close_editor_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            // Sound Selector Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x66000000))
                    .clickable { activeTool = EditorToolTab.SOUNDS }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("select_sound_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TokCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = selectedSound.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { /* Toggle camera flash simulation */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x55000000))
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }
        }

        // Right Editing Tools Toolbar (TikTok style)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 90.dp, end = 12.dp)
        ) {
            EditorSidebarAction(
                icon = Icons.Default.FlipCameraAndroid,
                label = "Flip",
                testTag = "editor_flip_button",
                onClick = { }
            )
            EditorSidebarAction(
                icon = Icons.Default.Speed,
                label = selectedSpeed,
                isActive = activeTool == EditorToolTab.SPEED,
                testTag = "editor_speed_button",
                onClick = { activeTool = if (activeTool == EditorToolTab.SPEED) EditorToolTab.NONE else EditorToolTab.SPEED }
            )
            EditorSidebarAction(
                icon = Icons.Default.AutoAwesome,
                label = "Filters",
                isActive = activeTool == EditorToolTab.FILTERS,
                testTag = "editor_filters_button",
                onClick = { activeTool = if (activeTool == EditorToolTab.FILTERS) EditorToolTab.NONE else EditorToolTab.FILTERS }
            )
            EditorSidebarAction(
                icon = Icons.Default.ElectricBolt,
                label = "Watermark",
                isActive = activeTool == EditorToolTab.WATERMARK,
                testTag = "editor_watermark_button",
                onClick = { activeTool = if (activeTool == EditorToolTab.WATERMARK) EditorToolTab.NONE else EditorToolTab.WATERMARK }
            )
            EditorSidebarAction(
                icon = Icons.Default.TextFields,
                label = "Text",
                isActive = activeTool == EditorToolTab.TEXT,
                testTag = "editor_text_button",
                onClick = { activeTool = if (activeTool == EditorToolTab.TEXT) EditorToolTab.NONE else EditorToolTab.TEXT }
            )
        }

        // Bottom Capture / Post Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contextual Drawer for Active Tool
            when (activeTool) {
                EditorToolTab.FILTERS -> {
                    FiltersDrawer(
                        filters = SampleData.filters,
                        selectedFilter = selectedFilter,
                        intensity = filterIntensity,
                        onSelectFilter = { selectedFilter = it },
                        onIntensityChange = { filterIntensity = it },
                        onClose = { activeTool = EditorToolTab.NONE }
                    )
                }
                EditorToolTab.SPEED -> {
                    SpeedSelectorRow(
                        selected = selectedSpeed,
                        onSelect = {
                            selectedSpeed = it
                            activeTool = EditorToolTab.NONE
                        }
                    )
                }
                EditorToolTab.TEXT -> {
                    TextOverlayEditor(
                        text = customOverlayText,
                        onTextChange = { customOverlayText = it },
                        onColorChange = { overlayTextColor = it },
                        onDone = { activeTool = EditorToolTab.NONE }
                    )
                }
                EditorToolTab.WATERMARK -> {
                    WatermarkConfigDrawer(
                        currentStyle = selectedWatermarkStyle,
                        currentPosition = watermarkPosition,
                        onStyleSelect = { selectedWatermarkStyle = it },
                        onPositionSelect = { watermarkPosition = it },
                        onDone = { activeTool = EditorToolTab.NONE }
                    )
                }
                EditorToolTab.SOUNDS -> {
                    SoundPickerDrawer(
                        soundTracks = SampleData.soundTracks,
                        selectedSound = selectedSound,
                        onSelectSound = {
                            selectedSound = it
                            activeTool = EditorToolTab.NONE
                        },
                        onClose = { activeTool = EditorToolTab.NONE }
                    )
                }
                EditorToolTab.PUBLISH -> {
                    PublishSheet(
                        caption = captionText,
                        onCaptionChange = { captionText = it },
                        selectedFilter = selectedFilter.name,
                        watermarkStyle = selectedWatermarkStyle,
                        soundTitle = selectedSound.title,
                        onPublish = {
                            val newPost = VideoPost(
                                id = "post_${System.currentTimeMillis()}",
                                creator = currentUser ?: SampleData.developerUser,
                                title = captionText.ifBlank { "New Clip with Asifofc Watermark" },
                                caption = captionText.ifBlank { "Created with TokPulse Studio ⚡ Watermark: Asifofc #TokPulse #Viral" },
                                hashtags = listOf("TokPulse", "Asifofc", "Shorts", "Creative"),
                                soundTitle = selectedSound.title,
                                soundAuthor = selectedSound.artist,
                                coverImageRes = R.drawable.tok_clip_cyberpunk,
                                dynamicTheme = "cyberpunk",
                                likesCount = 1,
                                commentsCount = 0,
                                sharesCount = 0,
                                bookmarksCount = 0,
                                isLiked = true,
                                isBookmarked = false,
                                isFollowing = false,
                                watermarkText = "Asifofc",
                                watermarkStyle = selectedWatermarkStyle,
                                durationSeconds = 15,
                                filterApplied = selectedFilter.name
                            )
                            onPublishVideo(newPost)
                        },
                        onBack = { activeTool = EditorToolTab.NONE }
                    )
                }
                EditorToolTab.NONE -> {
                    // Default Recording Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery Upload Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    mediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                                .testTag("upload_media_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x55000000))
                                    .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Upload",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Upload", color = Color.White, fontSize = 11.sp)
                        }

                        // Big Red TikTok Record Shutter
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) TokCyan else TokPink)
                                .clickable {
                                    if (isRecording) {
                                        isRecording = false
                                        activeTool = EditorToolTab.PUBLISH
                                    } else {
                                        isRecording = true
                                    }
                                }
                                .testTag("record_shutter_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRecording) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                )
                            }
                        }

                        // Next / Done Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { activeTool = EditorToolTab.PUBLISH }
                                .testTag("editor_next_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(TokPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Next", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorSidebarAction(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
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
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) TokPink else Color(0x55000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FiltersDrawer(
    filters: List<VideoFilter>,
    selectedFilter: VideoFilter,
    intensity: Float,
    onSelectFilter: (VideoFilter) -> Unit,
    onIntensityChange: (Float) -> Unit,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters Studio (${selectedFilter.name})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFFA0A0AB))
                }
            }

            // Intensity slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Text(text = "Intensity", color = Color(0xFFC0C2D2), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = intensity,
                    onValueChange = onIntensityChange,
                    valueRange = 0.2f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = TokPink,
                        activeTrackColor = TokPink,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(intensity * 100).toInt()}%",
                    color = TokCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp)
                )
            }

            // Filter Badges
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { filter ->
                    val isSelected = filter.id == selectedFilter.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectFilter(filter) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(filter.primaryColorHex), Color(filter.secondaryColorHex))
                                    )
                                )
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = filter.name,
                            color = if (isSelected) TokCyan else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedSelectorRow(
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf("0.5x", "1x", "2x", "3x").forEach { speed ->
                val isSelected = speed == selected
                Text(
                    text = speed,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) TokCyan else Color.Transparent)
                        .clickable { onSelect(speed) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun WatermarkConfigDrawer(
    currentStyle: WatermarkStyle,
    currentPosition: Alignment,
    onStyleSelect: (WatermarkStyle) -> Unit,
    onPositionSelect: (Alignment) -> Unit,
    onDone: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asifofc Watermark Customizer",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                IconButton(onClick = onDone, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Done", tint = Color(0xFFA0A0AB))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Badge Style", color = Color(0xFFA0A2B2), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(WatermarkStyle.values()) { style ->
                    val isSelected = style == currentStyle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TokPink else TokSurfaceVariant)
                            .clickable { onStyleSelect(style) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = style.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Placement Corner", color = Color(0xFFA0A2B2), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Pair("Top Left", Alignment.TopStart),
                    Pair("Top Right", Alignment.TopEnd),
                    Pair("Bottom Left", Alignment.BottomStart),
                    Pair("Bottom Right", Alignment.BottomEnd)
                ).forEach { (label, align) ->
                    val isSelected = align == currentPosition
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TokCyan else TokSurfaceVariant)
                            .clickable { onPositionSelect(align) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextOverlayEditor(
    text: String,
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onDone: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Add Text Overlay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = onDone, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = TokCyan)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Type sticker text (e.g. Asifofc Beats 🔥)") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TokSurfaceVariant,
                    unfocusedContainerColor = TokSurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = TokCyan
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Color.White, TokCyan, TokPink, Color(0xFFFFEB3B), Color(0xFF76FF03)).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onColorChange(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundPickerDrawer(
    soundTracks: List<SoundTrack>,
    selectedSound: SoundTrack,
    onSelectSound: (SoundTrack) -> Unit,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Trending TokPulse Sounds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFFA0A0AB))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            soundTracks.forEach { track ->
                val isSelected = track.id == selectedSound.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) TokSurfaceVariant else Color.Transparent)
                        .clickable { onSelectSound(track) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isSelected) TokPink else TokCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = track.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "${track.artist} • ${track.usageCount}", color = Color(0xFF888998), fontSize = 11.sp)
                    }
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = TokCyan)
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishSheet(
    caption: String,
    onCaptionChange: (String) -> Unit,
    selectedFilter: String,
    watermarkStyle: WatermarkStyle,
    soundTitle: String,
    onPublish: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = TokSurface),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Post Video to Feed",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = onCaptionChange,
                placeholder = { Text("Write a caption and add hashtags #fyp #asifofc...", color = Color(0xFF70717D), fontSize = 13.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TokSurfaceVariant,
                    unfocusedContainerColor = TokSurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = TokCyan
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("video_caption_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata summary pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TokSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Filter: $selectedFilter", color = TokCyan, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TokSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Watermark: Asifofc (${watermarkStyle.displayName})", color = TokPink, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = TokSurfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Edit More", color = Color.White)
                }
                Button(
                    onClick = onPublish,
                    colors = ButtonDefaults.buttonColors(containerColor = TokPink),
                    modifier = Modifier.weight(1.5f).testTag("publish_post_button")
                ) {
                    Text(text = "Post to TokPulse 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
