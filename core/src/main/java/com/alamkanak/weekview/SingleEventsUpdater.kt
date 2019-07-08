package com.alamkanak.weekview

import android.graphics.RectF
import java.util.Calendar

internal class SingleEventsUpdater<T>(
    private val view: WeekView<T>,
    private val config: WeekViewConfigWrapper,
    private val cache: WeekViewCache<T>
) : Updater {

    private val rectCalculator = EventChipRectCalculator<T>(config)

    override fun isRequired() = true

    override fun update(drawingContext: DrawingContext) {
        drawingContext
            .dateRangeWithStartPixels
            .forEach { (date, startPixel) ->
                calculateRectsForEventsOnDate(date, startPixel)
            }
    }

    private fun calculateRectsForEventsOnDate(
        date: Calendar,
        startPixel: Float
    ) {
        cache.eventCache.normalEventChipsByDate(date)
            .filter { it.event.isWithin(config.minHour, config.maxHour) }
            .forEach {
                val chipRect = rectCalculator.calculateSingleEvent(it, startPixel)
                if (chipRect.isValidSingleEventRect) {
                    it.rect = chipRect
                } else {
                    it.rect = null
                }
            }
    }

    private val RectF.isValidSingleEventRect: Boolean
        get() = (left < right
            && left < view.width
            && top < view.height
            && right > config.timeColumnWidth
            && bottom > config.headerHeight)

}