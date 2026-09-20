package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Available color modes for kaleidoscope patterns.
 */
enum class ColorMode(
    val title: String,
    val description: String,
    val previewColors: List<Color>
) {
    RAINBOW(
        title = "Gökkuşağı Prizması",
        description = "Açı ve zamana göre değişen spektrum",
        previewColors = listOf(
            Color(0xFFFF0055),
            Color(0xFFFF9900),
            Color(0xFFFFFF00),
            Color(0xFF00FF66),
            Color(0xFF00CCFF),
            Color(0xFF9900FF)
        )
    ),
    NEON_CYBER(
        title = "Neon Siber",
        description = "Elektrik camgöbeği, mor ve asit pembesi",
        previewColors = listOf(
            Color(0xFF00F5FF),
            Color(0xFFFF007F),
            Color(0xFF9D00FF),
            Color(0xFF39FF14)
        )
    ),
    AURORA(
        title = "Kutup Işığı",
        description = "Zümrüt yeşili, turkuaz ve yıldız moru",
        previewColors = listOf(
            Color(0xFF00FF87),
            Color(0xFF60EFFF),
            Color(0xFF7B2CBF),
            Color(0xFF10002B)
        )
    ),
    SUNSET(
        title = "Gün Batımı Ateşi",
        description = "Kızıl kor, kehribar ve lav pembesi",
        previewColors = listOf(
            Color(0xFFFF3366),
            Color(0xFFFF6600),
            Color(0xFFFFCC00),
            Color(0xFF990033)
        )
    ),
    GOLD(
        title = "Altın & Kehribar",
        description = "Asil saray altını ve bronz ışıltılar",
        previewColors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA000),
            Color(0xFFFFE082),
            Color(0xFFFF6F00)
        )
    ),
    OCEAN(
        title = "Okyanus Derinliği",
        description = "Biyolüminesan deniz mavisi ve safir",
        previewColors = listOf(
            Color(0xFF00E5FF),
            Color(0xFF0077B6),
            Color(0xFF023E8A),
            Color(0xFF48CAE4)
        )
    ),
    PASTEL(
        title = "Pastel Rüya",
        description = "Yumuşak lavanta, şeftali ve nane tonları",
        previewColors = listOf(
            Color(0xFFFFC6FF),
            Color(0xFFBDB2FF),
            Color(0xFFA0C4FF),
            Color(0xFFCAFFBF)
        )
    ),
    DIAMOND(
        title = "Gümüş Pırlanta",
        description = "Kristal beyaz, platin ve buz mavisi yansımalar",
        previewColors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFE0F7FA),
            Color(0xFFB0BEC5),
            Color(0xFF80DEEA)
        )
    );

    /**
     * Calculates color based on progress, angle, distance and global time offset.
     */
    fun getColor(progress: Float, angle: Float, distanceRatio: Float, timeOffset: Float): Int {
        return when (this) {
            RAINBOW -> {
                // Calculate HSV hue based on angle + distance + time
                val hue = ((angle * 180f / PI.toFloat() + timeOffset * 40f + distanceRatio * 180f) % 360f + 360f) % 360f
                val hsv = floatArrayOf(hue, 0.92f, 1.0f)
                android.graphics.Color.HSVToColor(hsv)
            }
            NEON_CYBER -> {
                val cycle = ((progress * 3f + timeOffset * 0.5f + distanceRatio) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFF00F5FF.toInt(),
                    0xFFFF007F.toInt(),
                    0xFF9D00FF.toInt(),
                    0xFF39FF14.toInt(),
                    0xFF00F5FF.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            AURORA -> {
                val cycle = ((progress * 2.5f + angle / (2 * PI.toFloat()) + timeOffset * 0.3f) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFF00FF87.toInt(),
                    0xFF60EFFF.toInt(),
                    0xFF7B2CBF.toInt(),
                    0xFF00FF87.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            SUNSET -> {
                val cycle = ((distanceRatio * 1.5f + progress * 0.8f + timeOffset * 0.4f) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFFFF3366.toInt(),
                    0xFFFF6600.toInt(),
                    0xFFFFCC00.toInt(),
                    0xFFFF1744.toInt(),
                    0xFFFF3366.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            GOLD -> {
                val cycle = ((angle / (2 * PI.toFloat()) * 2f + progress + timeOffset * 0.2f) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFFFFD700.toInt(),
                    0xFFFFA000.toInt(),
                    0xFFFFF9C4.toInt(),
                    0xFFFF8F00.toInt(),
                    0xFFFFD700.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            OCEAN -> {
                val cycle = ((distanceRatio * 2f + progress * 0.7f + timeOffset * 0.3f) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFF00E5FF.toInt(),
                    0xFF0077B6.toInt(),
                    0xFF48CAE4.toInt(),
                    0xFF023E8A.toInt(),
                    0xFF00E5FF.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            PASTEL -> {
                val cycle = ((progress * 2f + angle / (2 * PI.toFloat())) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFFFFC6FF.toInt(),
                    0xFFBDB2FF.toInt(),
                    0xFFA0C4FF.toInt(),
                    0xFFCAFFBF.toInt(),
                    0xFFFFC6FF.toInt()
                )
                interpolatePalette(palette, cycle)
            }
            DIAMOND -> {
                val cycle = ((distanceRatio * 3f + progress * 1.5f) % 1f + 1f) % 1f
                val palette = intArrayOf(
                    0xFFFFFFFF.toInt(),
                    0xFFE0F7FA.toInt(),
                    0xFFB0BEC5.toInt(),
                    0xFF80DEEA.toInt(),
                    0xFFFFFFFF.toInt()
                )
                interpolatePalette(palette, cycle)
            }
        }
    }

    private fun interpolatePalette(colors: IntArray, t: Float): Int {
        val scaled = t * (colors.size - 1)
        val index = scaled.toInt().coerceIn(0, colors.size - 2)
        val fraction = scaled - index
        val c1 = colors[index]
        val c2 = colors[index + 1]

        val a = (android.graphics.Color.alpha(c1) + fraction * (android.graphics.Color.alpha(c2) - android.graphics.Color.alpha(c1))).toInt()
        val r = (android.graphics.Color.red(c1) + fraction * (android.graphics.Color.red(c2) - android.graphics.Color.red(c1))).toInt()
        val g = (android.graphics.Color.green(c1) + fraction * (android.graphics.Color.green(c2) - android.graphics.Color.green(c1))).toInt()
        val b = (android.graphics.Color.blue(c1) + fraction * (android.graphics.Color.blue(c2) - android.graphics.Color.blue(c1))).toInt()

        return android.graphics.Color.argb(a, r, g, b)
    }
}

/**
 * Brush styles for drawing patterns.
 */
enum class BrushStyle(val title: String, val description: String) {
    GLOW_LINE("Neon Parlama", "Yumuşak ışık haleli parlayan neon çizgiler"),
    RIBBON("Akıcı Kurdele", "Hıza göre kalınlığı değişen pürüzsüz şeritler"),
    STARDUST("Yıldız Tozu", "Simetrik ışıltılı noktacıklar ve parçacıklar"),
    CRYSTAL_FACET("Kristal Prizma", "Geometrik fasetli elmas yansımalar")
}

/**
 * Drawing accumulation modes.
 */
enum class CanvasMode(val title: String, val description: String) {
    PERSISTENT("Kalıcı Mandala", "Desenler katman katman birikir, sanat eseri oluşturur"),
    FLOWING_TRAIL("Akışkan İzler", "Desenler arkasında parlayan kaybolan izler bırakır")
}

/**
 * Configuration for the kaleidoscope generator.
 */
data class KaleidoscopeConfig(
    val segments: Int = 12,
    val mirrorSymmetry: Boolean = true,
    val colorMode: ColorMode = ColorMode.RAINBOW,
    val brushStyle: BrushStyle = BrushStyle.GLOW_LINE,
    val brushSize: Float = 14f,
    val canvasMode: CanvasMode = CanvasMode.PERSISTENT,
    val isFrozen: Boolean = false,
    val autoSpin: Boolean = false,
    val spinSpeed: Float = 0.5f,
    val showGuideLines: Boolean = false,
    val glowIntensity: Float = 1.0f,
    val backgroundDarkness: Float = 0.98f
)
