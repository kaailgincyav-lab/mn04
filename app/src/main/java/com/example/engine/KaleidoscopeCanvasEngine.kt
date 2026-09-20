package com.example.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.BrushStyle
import com.example.model.CanvasMode
import com.example.model.KaleidoscopeConfig
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class KaleidoscopeCanvasEngine {

    private var canvasWidth: Int = 1080
    private var canvasHeight: Int = 1080
    private var centerX: Float = 540f
    private var centerY: Float = 540f

    // Master bitmap where drawing is persisted
    private var bitmap: Bitmap? = null
    private var nativeCanvas: Canvas? = null

    // Background color: deep space obsidian
    private val backgroundColor = AndroidColor.argb(255, 10, 8, 20)

    // Paints
    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = AndroidColor.argb(55, 180, 210, 255)
        strokeWidth = 1.2f
    }

    private val fadePaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        color = AndroidColor.argb(18, 10, 8, 20)
    }

    // Touch tracking
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var isFirstTouch: Boolean = true
    private var strokeDistanceAcc: Float = 0f
    private var globalTime: Float = 0f

    // Auto-flow simulation state
    private var autoFlowAngle: Float = 0f
    private var autoFlowRadius1: Float = 160f
    private var autoFlowRadius2: Float = 110f

    /**
     * Initializes or resizes the drawing surface.
     */
    fun updateSize(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        if (width == canvasWidth && height == canvasHeight && bitmap != null) return

        canvasWidth = width
        canvasHeight = height
        centerX = width / 2f
        centerY = height / 2f

        val oldBitmap = bitmap
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val newCanvas = Canvas(newBitmap)
        newCanvas.drawColor(backgroundColor)

        // Transfer existing drawing scaled if available
        if (oldBitmap != null && !oldBitmap.isRecycled) {
            newCanvas.drawBitmap(oldBitmap, 0f, 0f, null)
            oldBitmap.recycle()
        } else {
            // Draw initial demo blooming mandala
            drawInitialMandala(newCanvas)
        }

        bitmap = newBitmap
        nativeCanvas = newCanvas
    }

    fun getBitmap(): Bitmap? = bitmap

    /**
     * Clears the kaleidoscope drawing canvas to clean dark obsidian.
     */
    fun clear() {
        nativeCanvas?.drawColor(backgroundColor)
    }

    /**
     * Resets touch tracking on new gesture.
     */
    fun startStroke(x: Float, y: Float) {
        lastX = x
        lastY = y
        isFirstTouch = false
    }

    /**
     * Draws interactive symmetrical strokes from last touch to current touch.
     */
    fun addStroke(currentX: Float, currentY: Float, config: KaleidoscopeConfig) {
        if (config.isFrozen) return
        val canvas = nativeCanvas ?: return

        if (isFirstTouch) {
            startStroke(currentX, currentY)
            return
        }

        val dx = currentX - lastX
        val dy = currentY - lastY
        val stepDist = hypot(dx, dy)
        if (stepDist < 1.0f) return

        strokeDistanceAcc += stepDist
        globalTime += 0.03f

        renderSymmetricStroke(
            canvas = canvas,
            x1 = lastX,
            y1 = lastY,
            x2 = currentX,
            y2 = currentY,
            config = config,
            velocity = stepDist
        )

        lastX = currentX
        lastY = currentY
    }

    /**
     * Finishes the current stroke.
     */
    fun endStroke() {
        isFirstTouch = true
    }

    /**
     * Performs a tick for animations:
     * - In FLOWING_TRAIL mode, slowly fades previous strokes.
     * - Updates internal time.
     */
    fun onFrameTick(config: KaleidoscopeConfig) {
        if (config.isFrozen) return
        globalTime += 0.02f

        if (config.canvasMode == CanvasMode.FLOWING_TRAIL) {
            nativeCanvas?.drawRect(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), fadePaint)
        }
    }

    /**
     * Mathematical kaleidoscope symmetry projection.
     */
    private fun renderSymmetricStroke(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        config: KaleidoscopeConfig,
        velocity: Float
    ) {
        val n = config.segments.coerceIn(3, 36)
        val anglePerSector = (2 * PI / n).toFloat()

        // Relative coordinates from center
        val relX1 = x1 - centerX
        val relY1 = y1 - centerY
        val relX2 = x2 - centerX
        val relY2 = y2 - centerY

        val r1 = hypot(relX1, relY1)
        val theta1 = atan2(relY1, relX1)
        val r2 = hypot(relX2, relY2)
        val theta2 = atan2(relY2, relX2)

        val maxRadius = hypot(centerX, centerY).coerceAtLeast(1f)
        val distRatio = (r1 / maxRadius).coerceIn(0f, 1f)

        // Color calculation
        val progress = (strokeDistanceAcc / 800f) % 1.0f
        val argbColor = config.colorMode.getColor(progress, theta1, distRatio, globalTime)

        // Adjust stroke width based on brush style & velocity
        val baseWidth = config.brushSize
        val dynamicWidth = when (config.brushStyle) {
            BrushStyle.RIBBON -> (baseWidth * (1f + (velocity / 20f).coerceIn(0f, 2.5f))).coerceIn(4f, 65f)
            BrushStyle.STARDUST -> baseWidth * 0.75f
            BrushStyle.CRYSTAL_FACET -> baseWidth * 1.1f
            BrushStyle.GLOW_LINE -> baseWidth
        }

        corePaint.strokeWidth = dynamicWidth
        corePaint.color = argbColor

        glowPaint.strokeWidth = dynamicWidth * 2.4f
        val glowAlpha = (110 * config.glowIntensity).toInt().coerceIn(10, 200)
        glowPaint.color = AndroidColor.argb(
            glowAlpha,
            AndroidColor.red(argbColor),
            AndroidColor.green(argbColor),
            AndroidColor.blue(argbColor)
        )

        fillPaint.color = argbColor

        // Symmetrical iterations
        for (i in 0 until n) {
            val sectorRot = i * anglePerSector

            // 1. Direct rotation
            val p1RotAngle = theta1 + sectorRot
            val p2RotAngle = theta2 + sectorRot
            val px1 = centerX + r1 * cos(p1RotAngle)
            val py1 = centerY + r1 * sin(p1RotAngle)
            val px2 = centerX + r2 * cos(p2RotAngle)
            val py2 = centerY + r2 * sin(p2RotAngle)

            drawSegmentPrimitive(canvas, px1, py1, px2, py2, config.brushStyle, dynamicWidth, r1, theta1)

            // 2. Mirror reflection across sector bisector
            if (config.mirrorSymmetry) {
                val p1MirrorAngle = -theta1 + sectorRot
                val p2MirrorAngle = -theta2 + sectorRot
                val mpx1 = centerX + r1 * cos(p1MirrorAngle)
                val mpy1 = centerY + r1 * sin(p1MirrorAngle)
                val mpx2 = centerX + r2 * cos(p2MirrorAngle)
                val mpy2 = centerY + r2 * sin(p2MirrorAngle)

                drawSegmentPrimitive(canvas, mpx1, mpy1, mpx2, mpy2, config.brushStyle, dynamicWidth, r1, -theta1)
            }
        }
    }

    private fun drawSegmentPrimitive(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        brushStyle: BrushStyle,
        strokeWidth: Float,
        radius: Float,
        angle: Float
    ) {
        when (brushStyle) {
            BrushStyle.GLOW_LINE -> {
                // Soft glow underlay
                canvas.drawLine(x1, y1, x2, y2, glowPaint)
                // Sharp core
                canvas.drawLine(x1, y1, x2, y2, corePaint)
            }
            BrushStyle.RIBBON -> {
                canvas.drawLine(x1, y1, x2, y2, glowPaint)
                canvas.drawLine(x1, y1, x2, y2, corePaint)
            }
            BrushStyle.STARDUST -> {
                // Sparkle dots & starlight
                val midX = (x1 + x2) / 2f
                val midY = (y1 + y2) / 2f
                val dotRadius = strokeWidth * 0.55f
                canvas.drawCircle(midX, midY, dotRadius, glowPaint)
                canvas.drawCircle(midX, midY, dotRadius * 0.6f, fillPaint)

                // Occasional star diamond sparkle
                if (strokeDistanceAcc.toInt() % 16 < 4) {
                    val diamondPath = Path().apply {
                        moveTo(midX, midY - dotRadius * 1.8f)
                        lineTo(midX + dotRadius * 0.8f, midY)
                        lineTo(midX, midY + dotRadius * 1.8f)
                        lineTo(midX - dotRadius * 0.8f, midY)
                        close()
                    }
                    canvas.drawPath(diamondPath, fillPaint)
                }
            }
            BrushStyle.CRYSTAL_FACET -> {
                // Geometric crystal facet
                val facetPath = Path().apply {
                    moveTo(x1, y1)
                    lineTo(x2, y2)
                    // Offset toward or away from center
                    val factor = 0.94f
                    lineTo((x2 - centerX) * factor + centerX, (y2 - centerY) * factor + centerY)
                    lineTo((x1 - centerX) * factor + centerX, (y1 - centerY) * factor + centerY)
                    close()
                }
                val facetAlphaPaint = Paint(fillPaint).apply {
                    alpha = 65
                }
                canvas.drawPath(facetPath, facetAlphaPaint)
                canvas.drawLine(x1, y1, x2, y2, corePaint)
            }
        }
    }

    /**
     * Draws decorative guide lines showing kaleidoscope sectors if enabled.
     */
    fun drawGuides(composeDrawScopeCanvas: androidx.compose.ui.graphics.Canvas, config: KaleidoscopeConfig) {
        if (!config.showGuideLines) return

        val n = config.segments
        val angleStep = (2 * PI / n).toFloat()
        val maxR = hypot(centerX, centerY)

        val nativeC = composeDrawScopeCanvas.nativeCanvas
        for (i in 0 until n) {
            val ang = i * angleStep
            val targetX = centerX + maxR * cos(ang)
            val targetY = centerY + maxR * sin(ang)
            nativeC.drawLine(centerX, centerY, targetX, targetY, guidePaint)
        }

        // Concentric distance rings
        val rings = 4
        for (r in 1..rings) {
            val radius = (maxR / (rings + 1)) * r
            nativeC.drawCircle(centerX, centerY, radius, guidePaint)
        }
    }

    /**
     * Generates a mesmerizing blooming random kaleidoscope pattern.
     */
    fun generateRandomBloom(config: KaleidoscopeConfig) {
        val canvas = nativeCanvas ?: return
        clear()

        val steps = 280
        val petals = Random.nextInt(3, 9)
        val frequency = Random.nextFloat() * 2f + 1f
        val maxOrbit = centerX.coerceAtMost(centerY) * 0.82f

        var prevX: Float? = null
        var prevY: Float? = null

        for (step in 0..steps) {
            val t = (step.toFloat() / steps) * (2 * PI.toFloat() * frequency)
            val modulation = cos(petals * t) * 0.45f + 0.55f
            val r = maxOrbit * modulation
            val px = centerX + r * cos(t)
            val py = centerY + r * sin(t)

            if (prevX != null && prevY != null) {
                strokeDistanceAcc += 14f
                renderSymmetricStroke(
                    canvas = canvas,
                    x1 = prevX,
                    y1 = prevY,
                    x2 = px,
                    y2 = py,
                    config = config,
                    velocity = 8f
                )
            }
            prevX = px
            prevY = py
        }
    }

    /**
     * Initial greeting mandala so the user sees something beautiful instantly on start.
     */
    private fun drawInitialMandala(canvas: Canvas) {
        val initialConfig = KaleidoscopeConfig(
            segments = 12,
            mirrorSymmetry = true,
            brushStyle = BrushStyle.GLOW_LINE,
            brushSize = 8f
        )
        val steps = 240
        val petals = 5
        val maxOrbit = centerX.coerceAtMost(centerY) * 0.75f

        var prevX: Float? = null
        var prevY: Float? = null

        for (step in 0..steps) {
            val t = (step.toFloat() / steps) * (2 * PI.toFloat() * 2f)
            val modulation = sin(petals * t) * 0.4f + 0.6f
            val r = maxOrbit * modulation
            val px = centerX + r * cos(t)
            val py = centerY + r * sin(t)

            if (prevX != null && prevY != null) {
                strokeDistanceAcc += 10f
                renderSymmetricStroke(
                    canvas = canvas,
                    x1 = prevX,
                    y1 = prevY,
                    x2 = px,
                    y2 = py,
                    config = initialConfig,
                    velocity = 6f
                )
            }
            prevX = px
            prevY = py
        }
    }
}
