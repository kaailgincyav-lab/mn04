package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.KaleidoscopeCanvasEngine
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.StudioTextTertiary
import com.example.ui.components.BottomFloatingBar
import com.example.ui.components.ExportPreviewDialog
import com.example.ui.components.SettingsBottomSheet
import com.example.ui.components.TopFloatingBar
import com.example.ui.components.ZenRestoreButton

@Composable
fun KaleidoscopeScreen(
    viewModel: KaleidoscopeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isExportDialogVisible by viewModel.isExportDialogVisible.collectAsState()
    val exportedBitmap by viewModel.exportedBitmap.collectAsState()
    val isHudVisible by viewModel.isHudVisible.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val engine = remember { KaleidoscopeCanvasEngine() }
    val snackbarHostState = remember { SnackbarHostState() }

    var hasUserTouchedOnce by remember { mutableStateOf(false) }
    var renderTrigger by remember { mutableIntStateOf(0) }
    var spinRotation by remember { mutableFloatStateOf(0f) }

    // Show toast message if updated
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    // Animation & auto-spin frame loop
    LaunchedEffect(config.isFrozen, config.autoSpin, config.canvasMode) {
        if (config.isFrozen || (!config.autoSpin && config.canvasMode != com.example.model.CanvasMode.FLOWING_TRAIL)) {
            return@LaunchedEffect
        }
        var lastNano = System.nanoTime()
        while (true) {
            withFrameNanos { now ->
                val dt = (now - lastNano) / 1_000_000_000f
                lastNano = now

                if (config.autoSpin) {
                    spinRotation = (spinRotation + dt * 15f * config.spinSpeed) % 360f
                }
                engine.onFrameTick(config)
                renderTrigger++
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBlack)
            .testTag("kaleidoscope_screen")
    ) {
        // Master Kaleidoscope Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("kaleidoscope_canvas")
                .onSizeChanged { size ->
                    if (size.width > 0 && size.height > 0) {
                        engine.updateSize(size.width, size.height)
                        renderTrigger++
                    }
                }
                .pointerInput(config.isFrozen) {
                    if (config.isFrozen) return@pointerInput

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        hasUserTouchedOnce = true
                        engine.startStroke(down.position.x, down.position.y)
                        renderTrigger++

                        do {
                            val event = awaitPointerEvent()
                            // Process any active pointer
                            val activePointers = event.changes.filter { it.pressed }
                            if (activePointers.isNotEmpty()) {
                                for (pointer in activePointers) {
                                    if (pointer.positionChange() != androidx.compose.ui.geometry.Offset.Zero) {
                                        engine.addStroke(pointer.position.x, pointer.position.y, config)
                                        pointer.consume()
                                    }
                                }
                                renderTrigger++
                            }
                        } while (event.changes.any { it.pressed })

                        engine.endStroke()
                        renderTrigger++
                    }
                }
        ) {
            // Read renderTrigger to ensure recomposition on each draw
            @Suppress("UNUSED_VARIABLE")
            val frame = renderTrigger

            val bmp = engine.getBitmap()
            if (bmp != null && !bmp.isRecycled) {
                if (config.autoSpin && spinRotation != 0f) {
                    rotate(degrees = spinRotation, pivot = center) {
                        drawImage(
                            image = bmp.asImageBitmap(),
                            dstOffset = IntOffset.Zero
                        )
                    }
                } else {
                    drawImage(
                        image = bmp.asImageBitmap(),
                        dstOffset = IntOffset.Zero
                    )
                }
            }

            // Draw guide lines overlay if enabled
            if (config.showGuideLines) {
                engine.drawGuides(drawContext.canvas, config)
            }
        }

        // First-launch tutorial cue (fades out gracefully on touch)
        AnimatedVisibility(
            visible = !hasUserTouchedOnce && isHudVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(StudioSurface.copy(alpha = 0.85f))
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Çizim yapmak için ekrana dokunun",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        fontSize = 13.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Top Floating HUD
        AnimatedVisibility(
            visible = isHudVisible,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopFloatingBar(
                config = config,
                onStepSegments = { delta -> viewModel.stepSegments(delta) },
                onToggleHud = { viewModel.toggleHud() },
                onOpenSettings = { viewModel.toggleSettings() }
            )
        }

        // Bottom Floating Action Dock
        AnimatedVisibility(
            visible = isHudVisible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomFloatingBar(
                config = config,
                onToggleFreeze = { viewModel.toggleFreeze() },
                onRandomize = { viewModel.randomize(engine) },
                onOpenSettings = { viewModel.toggleSettings() },
                onClearCanvas = { viewModel.clearCanvas(engine) },
                onExport = { viewModel.prepareExport(engine) },
                onStepSegments = { delta -> viewModel.stepSegments(delta) }
            )
        }

        // Zen Mode restore button when HUD is hidden
        AnimatedVisibility(
            visible = !isHudVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            ZenRestoreButton(onRestore = { viewModel.showHud() })
        }

        // Snackbar host for toast messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isHudVisible) 90.dp else 24.dp)
        )

        // Settings & Color Palette Bottom Sheet
        if (isSettingsOpen) {
            SettingsBottomSheet(
                config = config,
                onDismiss = { viewModel.toggleSettings() },
                onSegmentsChange = { viewModel.updateSegments(it) },
                onColorModeChange = { viewModel.setColorMode(it) },
                onBrushStyleChange = { viewModel.setBrushStyle(it) },
                onBrushSizeChange = { viewModel.setBrushSize(it) },
                onMirrorToggle = { viewModel.toggleMirror() },
                onAutoSpinToggle = { viewModel.toggleAutoSpin() },
                onCanvasModeToggle = { viewModel.toggleCanvasMode() },
                onGuideLinesToggle = { viewModel.toggleGuideLines() }
            )
        }

        // Export Masterpiece Preview Dialog
        if (isExportDialogVisible) {
            ExportPreviewDialog(
                bitmap = exportedBitmap,
                segments = config.segments,
                onDismiss = { viewModel.dismissExportDialog() },
                onSaveToGallery = { viewModel.saveExportedImage(context) },
                onShare = { viewModel.shareExportedImage(context) }
            )
        }
    }
}
