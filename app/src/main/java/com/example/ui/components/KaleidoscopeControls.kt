package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KaleidoscopeConfig
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioIce
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.StudioTextTertiary

@Composable
fun TopFloatingBar(
    config: KaleidoscopeConfig,
    onStepSegments: (Int) -> Unit,
    onToggleHud: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minimalist brand capsule
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(StudioSurface.copy(alpha = 0.85f))
                .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
                .clickable { onOpenSettings() }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (config.isFrozen) StudioAccent else Color(0xFF4ADE80))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "KALEİDOSKOP",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextPrimary,
                    letterSpacing = 2.sp,
                    fontSize = 11.sp
                )
            )

            if (config.isFrozen) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• DURAKLATILDI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = StudioAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Action utilities (Settings & Zen Mode)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quick Studio Settings Trigger
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(StudioSurface.copy(alpha = 0.85f))
                    .border(1.dp, StudioBorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Stüdyo Ayarları",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Minimalist Zen Mode (Hide HUD)
            IconButton(
                onClick = onToggleHud,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(StudioSurface.copy(alpha = 0.85f))
                    .border(1.dp, StudioBorderSubtle, CircleShape)
                    .testTag("toggle_hud_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Arayüzü Gizle",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BottomFloatingBar(
    config: KaleidoscopeConfig,
    onToggleFreeze: () -> Unit,
    onRandomize: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearCanvas: () -> Unit,
    onExport: () -> Unit,
    onStepSegments: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color.Black)
                .clip(RoundedCornerShape(26.dp))
                .background(StudioSurface.copy(alpha = 0.92f))
                .border(1.dp, StudioBorderSubtle, RoundedCornerShape(26.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. FREEZE / PLAY (Studio Shutter)
            StudioDockItem(
                icon = if (config.isFrozen) Icons.Default.PlayArrow else Icons.Default.Pause,
                label = if (config.isFrozen) "Sürdür" else "Dondur",
                isActive = config.isFrozen,
                activeColor = StudioAccent,
                onClick = onToggleFreeze,
                tag = "freeze_toggle_button"
            )

            // 2. SEGMENT STEPPER CAPSULE
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onStepSegments(-2) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("decrement_segments_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Bölüm Azalt",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onOpenSettings() }
                        .padding(horizontal = 6.dp)
                        .testTag("open_settings_via_segments")
                ) {
                    Text(
                        text = "${config.segments}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "DİLİM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = StudioTextTertiary,
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = { onStepSegments(2) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("increment_segments_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Bölüm Arttır",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // 3. COLOR PALETTE SWATCH
            StudioPaletteDockItem(
                config = config,
                onClick = onOpenSettings,
                tag = "open_settings_button"
            )

            // 4. RANDOMIZE (Karıştır)
            StudioDockItem(
                icon = Icons.Default.Casino,
                label = "Karıştır",
                isActive = false,
                activeColor = StudioTextPrimary,
                onClick = onRandomize,
                tag = "randomize_button"
            )

            // 5. CLEAR CANVAS (Temizle)
            StudioDockItem(
                icon = Icons.Default.DeleteSweep,
                label = "Temizle",
                isActive = false,
                activeColor = Color(0xFFF87171),
                onClick = onClearCanvas,
                tag = "clear_canvas_button"
            )

            // 6. EXPORT / SHARE (Dışa Aktar)
            StudioDockItem(
                icon = Icons.Default.FileDownload,
                label = "Paylaş",
                isActive = false,
                activeColor = StudioTextPrimary,
                onClick = onExport,
                tag = "export_image_button"
            )
        }
    }
}

@Composable
private fun StudioDockItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    tag: String
) {
    val contentColor by animateColorAsState(
        if (isActive) activeColor else StudioTextSecondary
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = 0.15f) else Color.Transparent)
                .border(
                    width = if (isActive) 1.dp else 0.dp,
                    color = if (isActive) activeColor.copy(alpha = 0.4f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor
            )
        )
    }
}

@Composable
private fun StudioPaletteDockItem(
    config: KaleidoscopeConfig,
    onClick: () -> Unit,
    tag: String
) {
    val paletteColors = config.colorMode.previewColors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, StudioBorderSubtle, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            if (paletteColors.size >= 2) paletteColors else listOf(StudioAccent, StudioIce)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Palet",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = StudioTextSecondary
            )
        )
    }
}

@Composable
fun ZenRestoreButton(
    onRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(16.dp)
            .clip(CircleShape)
            .background(StudioSurface.copy(alpha = 0.85f))
            .border(1.dp, StudioBorderSubtle, CircleShape)
            .clickable { onRestore() }
            .padding(10.dp)
            .testTag("zen_restore_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "Kontrolleri Göster",
            tint = StudioTextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

