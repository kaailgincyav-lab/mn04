package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.KaleidoscopeCanvasEngine
import com.example.model.BrushStyle
import com.example.model.CanvasMode
import com.example.model.ColorMode
import com.example.model.KaleidoscopeConfig
import com.example.util.ImageExportHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class KaleidoscopeViewModel : ViewModel() {

    private val _config = MutableStateFlow(KaleidoscopeConfig())
    val config: StateFlow<KaleidoscopeConfig> = _config.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isExportDialogVisible = MutableStateFlow(false)
    val isExportDialogVisible: StateFlow<Boolean> = _isExportDialogVisible.asStateFlow()

    private val _exportedBitmap = MutableStateFlow<Bitmap?>(null)
    val exportedBitmap: StateFlow<Bitmap?> = _exportedBitmap.asStateFlow()

    private val _isHudVisible = MutableStateFlow(true)
    val isHudVisible: StateFlow<Boolean> = _isHudVisible.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun updateSegments(newSegments: Int) {
        val clamped = newSegments.coerceIn(3, 36)
        _config.update { it.copy(segments = clamped) }
    }

    fun stepSegments(delta: Int) {
        val current = _config.value.segments
        updateSegments(current + delta)
    }

    fun setColorMode(mode: ColorMode) {
        _config.update { it.copy(colorMode = mode) }
    }

    fun setBrushStyle(style: BrushStyle) {
        _config.update { it.copy(brushStyle = style) }
    }

    fun setBrushSize(size: Float) {
        _config.update { it.copy(brushSize = size.coerceIn(4f, 48f)) }
    }

    fun toggleFreeze() {
        val next = !_config.value.isFrozen
        _config.update { it.copy(isFrozen = next) }
        _userMessage.value = if (next) "Desen Donduruldu ❄️" else "Desen Çözüldü ▶"
    }

    fun toggleMirror() {
        _config.update { it.copy(mirrorSymmetry = !it.mirrorSymmetry) }
    }

    fun toggleAutoSpin() {
        _config.update { it.copy(autoSpin = !it.autoSpin) }
    }

    fun toggleCanvasMode() {
        val next = if (_config.value.canvasMode == CanvasMode.PERSISTENT) {
            CanvasMode.FLOWING_TRAIL
        } else {
            CanvasMode.PERSISTENT
        }
        _config.update { it.copy(canvasMode = next) }
        _userMessage.value = if (next == CanvasMode.FLOWING_TRAIL) "Akışkan İzler Aktif ✨" else "Kalıcı Çizim Aktif 🎨"
    }

    fun toggleGuideLines() {
        _config.update { it.copy(showGuideLines = !it.showGuideLines) }
    }

    fun toggleSettings() {
        _isSettingsOpen.update { !it }
    }

    fun toggleHud() {
        _isHudVisible.update { !it }
    }

    fun showHud() {
        _isHudVisible.value = true
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    /**
     * Randomizes segments, color mode, brush style, mirror and generates an instant blooming pattern!
     */
    fun randomize(engine: KaleidoscopeCanvasEngine) {
        val segmentOptions = intArrayOf(6, 8, 10, 12, 14, 16, 18, 20, 24, 32)
        val randomSegments = segmentOptions.random()
        val randomColor = ColorMode.entries.random()
        val randomBrush = BrushStyle.entries.random()
        val randomMirror = Random.nextBoolean()

        _config.update {
            it.copy(
                segments = randomSegments,
                colorMode = randomColor,
                brushStyle = randomBrush,
                mirrorSymmetry = randomMirror,
                isFrozen = false
            )
        }

        engine.generateRandomBloom(_config.value)
        _userMessage.value = "Rastgeleleştirildi: $randomSegments Bölüm, ${randomColor.title} 🎲"
    }

    fun clearCanvas(engine: KaleidoscopeCanvasEngine) {
        engine.clear()
        _userMessage.value = "Tuval Temizlendi 🧹"
    }

    fun prepareExport(engine: KaleidoscopeCanvasEngine) {
        val currentBitmap = engine.getBitmap()
        if (currentBitmap != null && !currentBitmap.isRecycled) {
            // Make a clean immutable copy for the export preview
            val copy = currentBitmap.copy(Bitmap.Config.ARGB_8888, false)
            _exportedBitmap.value = copy
            _isExportDialogVisible.value = true
        } else {
            _userMessage.value = "Dışa aktarılacak görüntü bulunamadı"
        }
    }

    fun dismissExportDialog() {
        _isExportDialogVisible.value = false
    }

    fun saveExportedImage(context: Context) {
        val bitmap = _exportedBitmap.value ?: return
        viewModelScope.launch {
            val result = ImageExportHelper.saveBitmapToGallery(context, bitmap)
            result.onSuccess {
                _userMessage.value = "Görüntü galeriye başarıyla kaydedildi! 📥"
                _isExportDialogVisible.value = false
            }.onFailure { e ->
                _userMessage.value = "Kaydetme başarısız: ${e.localizedMessage}"
            }
        }
    }

    fun shareExportedImage(context: Context) {
        val bitmap = _exportedBitmap.value ?: return
        ImageExportHelper.shareBitmap(context, bitmap)
    }
}
