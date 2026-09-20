package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrushStyle
import com.example.model.CanvasMode
import com.example.model.ColorMode
import com.example.model.KaleidoscopeConfig
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.StudioTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    config: KaleidoscopeConfig,
    onDismiss: () -> Unit,
    onSegmentsChange: (Int) -> Unit,
    onColorModeChange: (ColorMode) -> Unit,
    onBrushStyleChange: (BrushStyle) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onMirrorToggle: () -> Unit,
    onAutoSpinToggle: () -> Unit,
    onCanvasModeToggle: () -> Unit,
    onGuideLinesToggle: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioSurface,
        contentColor = StudioTextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Minimalist Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stüdyo Ayarları",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            letterSpacing = 0.5.sp,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Simetri, palet ve fırça parametreleri",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioBorderSubtle, CircleShape)
                        .testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 1: SEGMENTS
            SectionHeader(title = "SİMETRİ EKSENLERİ", badge = "${config.segments} DİLİM")

            // Preset Segment Chips
            val presets = listOf(4, 6, 8, 10, 12, 16, 24, 32)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = config.segments == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSegmentsChange(preset) },
                        label = {
                            Text(
                                text = "$preset",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioAccent.copy(alpha = 0.18f),
                            selectedLabelColor = StudioAccent,
                            containerColor = StudioSurfaceElevated,
                            labelColor = StudioTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) StudioAccent else StudioBorderSubtle,
                            selectedBorderColor = StudioAccent,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.2.dp
                        ),
                        modifier = Modifier.testTag("segment_chip_$preset")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Continuous Slider
            Slider(
                value = config.segments.toFloat(),
                onValueChange = { onSegmentsChange(it.toInt()) },
                valueRange = 3f..36f,
                steps = 32,
                colors = SliderDefaults.colors(
                    thumbColor = StudioAccent,
                    activeTrackColor = StudioAccent,
                    inactiveTrackColor = StudioSurfaceElevated
                ),
                modifier = Modifier.testTag("segments_slider")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: COLOR PALETTES
            SectionHeader(title = "RENK PALETLERİ", badge = config.colorMode.title)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorMode.entries.forEach { mode ->
                    val isSelected = config.colorMode == mode
                    val borderColor by animateColorAsState(
                        if (isSelected) StudioAccent else StudioBorderSubtle
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) StudioSurfaceElevated else StudioSurfaceElevated.copy(alpha = 0.5f))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable { onColorModeChange(mode) }
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                            .testTag("color_mode_${mode.name}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Palette preview gradient dots
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                mode.previewColors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSelected) StudioTextPrimary else StudioTextSecondary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StudioTextTertiary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = StudioAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: BRUSH STYLES
            SectionHeader(title = "FIRÇA STİLİ", badge = config.brushStyle.title)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BrushStyle.entries.forEach { style ->
                    val isSelected = config.brushStyle == style
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) StudioAccent.copy(alpha = 0.14f) else StudioSurfaceElevated)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) StudioAccent else StudioBorderSubtle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onBrushStyleChange(style) }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag("brush_style_${style.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) StudioAccent else StudioTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 4: BRUSH SIZE
            SectionHeader(title = "FIRÇA KALINLIĞI", badge = "${config.brushSize.toInt()} PX")

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(StudioTextTertiary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Slider(
                    value = config.brushSize,
                    onValueChange = onBrushSizeChange,
                    valueRange = 4f..44f,
                    colors = SliderDefaults.colors(
                        thumbColor = StudioAccent,
                        activeTrackColor = StudioAccent,
                        inactiveTrackColor = StudioSurfaceElevated
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("brush_size_slider")
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(StudioTextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 5: TOGGLES
            SectionHeader(title = "SİMETRİ VE AKIŞ EFEKTLERİ", badge = null)

            ToggleRow(
                title = "Ayna Yansıması",
                subtitle = "Her sektör içine çift yönlü yansıma katarak zengin simetri oluşturur",
                checked = config.mirrorSymmetry,
                onCheckedChange = { onMirrorToggle() },
                tag = "mirror_toggle"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                title = "Akışkan İzler Modu",
                subtitle = "Çizimler arkasında yavaşça sönen yumuşak ışık izleri bırakır",
                checked = config.canvasMode == CanvasMode.FLOWING_TRAIL,
                onCheckedChange = { onCanvasModeToggle() },
                tag = "trail_fade_toggle"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                title = "Otomatik Dönüş",
                subtitle = "Tuval kendi etrafında yavaşça dönerek optik hareket sağlar",
                checked = config.autoSpin,
                onCheckedChange = { onAutoSpinToggle() },
                tag = "auto_spin_toggle"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                title = "Kılavuz Çizgileri",
                subtitle = "Simetri sektör sınırlarını ve merkez noktasını gösterir",
                checked = config.showGuideLines,
                onCheckedChange = { onGuideLinesToggle() },
                tag = "guide_lines_toggle"
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, badge: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = StudioTextTertiary,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
            )
        )
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = StudioAccent,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StudioSurfaceElevated.copy(alpha = 0.6f))
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 11.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = StudioTextPrimary,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = StudioTextTertiary,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StudioSurface,
                checkedTrackColor = StudioAccent,
                uncheckedThumbColor = StudioTextSecondary,
                uncheckedTrackColor = StudioSurfaceElevated
            )
        )
    }
}
