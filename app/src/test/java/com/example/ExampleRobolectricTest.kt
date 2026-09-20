package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BrushStyle
import com.example.model.ColorMode
import com.example.ui.KaleidoscopeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kaleidoskop", appName)
  }

  @Test
  fun `kaleidoscope viewmodel initial config and controls`() {
    val viewModel = KaleidoscopeViewModel()
    val initialConfig = viewModel.config.value
    assertEquals(12, initialConfig.segments)
    assertTrue(initialConfig.mirrorSymmetry)
    assertEquals(ColorMode.RAINBOW, initialConfig.colorMode)

    // Test segment step
    viewModel.stepSegments(2)
    assertEquals(14, viewModel.config.value.segments)

    // Test freeze toggle
    viewModel.toggleFreeze()
    assertTrue(viewModel.config.value.isFrozen)

    // Test color mode update
    viewModel.setColorMode(ColorMode.NEON_CYBER)
    assertEquals(ColorMode.NEON_CYBER, viewModel.config.value.colorMode)

    // Test brush style
    viewModel.setBrushStyle(BrushStyle.CRYSTAL_FACET)
    assertEquals(BrushStyle.CRYSTAL_FACET, viewModel.config.value.brushStyle)
  }
}

