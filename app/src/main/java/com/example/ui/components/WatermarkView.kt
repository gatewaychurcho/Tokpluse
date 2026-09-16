package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WatermarkStyle
import com.example.ui.theme.TokCyan
import com.example.ui.theme.TokPink

/**
 * Highly stylish designer watermark for "Asifofc".
 * Displays in feeds and video editor previews with animated neon glow and TikTok-inspired badge aesthetic.
 */
@Composable
fun WatermarkView(
    text: String = "Asifofc",
    style: WatermarkStyle = WatermarkStyle.NEON_CYBER,
    modifier: Modifier = Modifier,
    alpha: Float = 0.92f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "watermark_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    when (style) {
        WatermarkStyle.NEON_CYBER -> {
            Box(
                modifier = modifier
                    .shadow(elevation = (6 * glowPulse).dp, shape = RoundedCornerShape(20.dp), spotColor = TokCyan)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC0A0A14))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                TokCyan.copy(alpha = glowPulse),
                                TokPink.copy(alpha = 1f - (glowPulse * 0.3f))
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(TokCyan.copy(alpha = glowPulse))
                    )
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = TokPink,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp
                    )
                }
            }
        }

        WatermarkStyle.TIKTOK_CLASSIC -> {
            // Dual-tone TikTok pill watermark with cyan and pink shadow offset
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xB3000000))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TokCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "@$text",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        WatermarkStyle.MINIMAL_CORNER -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x80111118))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "• $text •",
                    color = Color(0xDDFFFFFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        WatermarkStyle.HOLO_GLITCH -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0x9925F4EE),
                                Color(0x999B51E0),
                                Color(0x99FE2C55)
                            )
                        )
                    )
                    .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "✦ $text ✦",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
