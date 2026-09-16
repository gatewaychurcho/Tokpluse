package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.VideoPost
import com.example.model.WatermarkStyle
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun VideoPlayerView(
    post: VideoPost,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isMuted by remember { mutableStateOf(false) }
    var showHeartAnimation by remember { mutableStateOf(false) }
    var heartPosition by remember { mutableStateOf(Offset.Zero) }

    // Video playback progress animation
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(50)
            progress = (progress + 0.004f) % 1f
        }
    }

    // Infinite transition for ambient video motion & watermark floating
    val infiniteTransition = rememberInfiniteTransition(label = "video_motion")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    // Watermark alternate corner drift (TikTok watermark shifts corners)
    val watermarkCornerShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "watermark_drift"
    )

    // Filter ColorMatrix setup
    val colorMatrix = remember(post.filterApplied) {
        when (post.filterApplied) {
            "Cyber Neon" -> ColorMatrix().apply {
                setToSaturation(1.3f)
            }
            "Vintage VHS" -> ColorMatrix(
                floatArrayOf(
                    1.1f, 0f, 0f, 0f, 15f,
                    0f, 0.95f, 0f, 0f, 5f,
                    0f, 0f, 0.85f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Golden Hour" -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0f, 0f, 0f, 20f,
                    0f, 1.05f, 0f, 0f, 10f,
                    0f, 0f, 0.8f, 0f, -10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "Noir B&W" -> ColorMatrix().apply {
                setToSaturation(0f)
            }
            "Pastel Dream" -> ColorMatrix().apply {
                setToSaturation(0.85f)
            }
            else -> ColorMatrix()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTogglePlay()
                    },
                    onDoubleTap = { tapOffset ->
                        heartPosition = tapOffset
                        showHeartAnimation = true
                        onDoubleTapLike()
                        coroutineScope.launch {
                            delay(800)
                            showHeartAnimation = false
                        }
                    }
                )
            }
    ) {
        // Video Visual Surface: Real cover image or animated dynamic canvas
        if (post.coverImageRes != null) {
            Image(
                painter = painterResource(id = post.coverImageRes),
                contentDescription = post.title,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Dynamic generative video backdrop for procedural posts
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF13091B),
                            Color(0xFF241442),
                            Color(0xFF0F0B1A)
                        )
                    )
                )

                // Animated glowing sound/energy ripples
                val numRings = 6
                for (i in 1..numRings) {
                    val radius = (canvasWidth * 0.35f) + (i * 35f) + (sin(Math.toRadians((waveOffset + i * 40).toDouble())).toFloat() * 15f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                TokPink.copy(alpha = 0.18f / i),
                                TokCyan.copy(alpha = 0.08f / i),
                                Color.Transparent
                            ),
                            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.45f),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.45f)
                    )
                }

                // Ambient light bars
                for (b in 0..12) {
                    val x = (b * (canvasWidth / 12f))
                    val heightMod = sin(Math.toRadians((waveOffset * 1.5 + b * 25).toDouble())).toFloat() * 80f
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(TokCyan.copy(alpha = 0.25f), TokPink.copy(alpha = 0.25f))
                        ),
                        start = Offset(x, canvasHeight * 0.5f - heightMod),
                        end = Offset(x, canvasHeight * 0.5f + heightMod),
                        strokeWidth = 4f
                    )
                }
            }
        }

        // Cyber / VHS Filter Overlay accents
        if (post.filterApplied == "Vintage VHS") {
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
                val lineSpacing = 6f
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5f
                    )
                    y += lineSpacing
                }
            }
        } else if (post.filterApplied == "Cyber Neon") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, TokCyan.copy(alpha = 0.06f), TokPink.copy(alpha = 0.12f)),
                            radius = 900f
                        )
                    )
            )
        }

        // Top and Bottom dark vignette for UI clarity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x99000000),
                        0.18f to Color.Transparent,
                        0.60f to Color.Transparent,
                        1.0f to Color(0xCC000000)
                    )
                )
        )

        // Watermark: stylish "Asifofc" watermark with authentic floating corner placement
        val watermarkAlignment = if (watermarkCornerShift > 0.5f) Alignment.TopStart else Alignment.BottomStart
        val watermarkPaddingMod = if (watermarkCornerShift > 0.5f) {
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 80.dp)
        } else {
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 120.dp)
        }

        WatermarkView(
            text = post.watermarkText,
            style = post.watermarkStyle,
            modifier = watermarkPaddingMod
        )

        // Paused Indicator Overlay
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Double-Tap Heart Burst Animation
        AnimatedVisibility(
            visible = showHeartAnimation,
            enter = fadeIn(tween(150)) + scaleIn(tween(250)),
            exit = fadeOut(tween(350)) + scaleOut(tween(350)),
            modifier = Modifier
                .offset {
                    IntOffset(
                        (heartPosition.x - 60).toInt().coerceAtLeast(0),
                        (heartPosition.y - 60).toInt().coerceAtLeast(0)
                    )
                }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = TokPink,
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.Transparent)
            )
        }

        // Mute / Unmute quick toggle button (top right)
        IconButton(
            onClick = { isMuted = !isMuted },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x55000000))
                .testTag("mute_toggle_button")
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Video Progress Bar at the very bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.5.dp)
                .background(Color(0x33FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.5.dp)
                    .background(Color.White)
            )
        }
    }
}
