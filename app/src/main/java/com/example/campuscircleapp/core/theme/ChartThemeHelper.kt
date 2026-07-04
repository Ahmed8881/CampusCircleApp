package com.example.campuscircleapp.core.theme

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.example.campuscircleapp.R
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet

object ChartThemeHelper {
    fun lineColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorChartLine)
    fun fillColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorChartFill)
    fun gridColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorChartGrid)
    fun textColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorChartText)
    fun headingColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorTextHeading)
    fun bodyColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorTextBody)
    fun mutedColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorTextMuted)
    fun surfaceColor(context: Context): Int = ThemeManager.resolveColorCompat(context, com.google.android.material.R.attr.colorSurface)
    fun brandPrimary(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorBrandPrimary)
    fun brandAccent(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorBrandAccent)
    fun brandBright(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorBrandBright)
    fun successColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorSuccess)
    fun warningColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorWarning)
    fun errorColor(context: Context): Int = ThemeManager.resolveColorCompat(context, R.attr.colorError)

    fun applyLineDataSetStyle(context: Context, dataSet: LineDataSet): LineDataSet {
        dataSet.color = lineColor(context)
        dataSet.setCircleColor(lineColor(context))
        dataSet.fillColor = fillColor(context)
        dataSet.highLightColor = lineColor(context)
        dataSet.valueTextColor = textColor(context)
        return dataSet
    }

    fun applyBarDataSetStyle(context: Context, dataSet: BarDataSet): BarDataSet {
        dataSet.color = lineColor(context)
        dataSet.valueTextColor = textColor(context)
        return dataSet
    }

    fun applyPieDataSetStyle(
        context: Context,
        dataSet: PieDataSet,
        colors: List<Int> = emptyList()
    ): PieDataSet {
        if (colors.isNotEmpty()) {
            dataSet.colors = colors
        } else {
            dataSet.colors = listOf(brandPrimary(context), brandAccent(context), brandBright(context))
        }
        dataSet.valueTextColor = headingColor(context)
        return dataSet
    }

    fun attendancePieColors(context: Context): Pair<Int, Int> {
        return brandPrimary(context) to mutedColor(context)
    }
}
