package ru.levkopo.vezdecodmobile.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.roundToInt


object ColorUtils {

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        return Color.argb((Color.alpha(color) * if(factor>1f) 1f else factor).roundToInt(),
            Color.red(color), Color.green(color), Color.blue(color))
    }
}