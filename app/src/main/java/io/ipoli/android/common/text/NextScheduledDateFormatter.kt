package io.ipoli.android.common.text

import android.content.Context
import io.ipoli.android.R
import io.ipoli.android.common.datetime.DateUtils
import io.ipoli.android.common.datetime.Time
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 8/24/17.
 */
object NextScheduledDateFormatter {
    fun format(context: Context, nextDate: LocalDate?, duration: Int, startTime: Time?): String {
        var nextText = ""
        if (nextDate == null) {
            return context.getString(R.string.unscheduled)
        } else {
            nextText = when {
                DateUtils.isTodayUTC(nextDate) -> context.getString(R.string.today)
                DateUtils.isTomorrowUTC(nextDate) -> context.getString(R.string.tomorrow)
                else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(DateUtils.toStartOfDay(nextDate))
            }
        }

        nextText += " "

        when {
            duration > 0 && startTime != null -> {
                val endTime = Time.plusMinutes(startTime, duration)
                nextText += startTime.toString() + " - " + endTime
            }
            duration > 0 -> nextText += String.format(context.getString(R.string.repeating_quest_for_time), DurationFormatter.formatReadable(context, duration))
            startTime != null -> nextText += startTime
        }
        return String.format(context.getString(R.string.repeating_quest_next), nextText)
    }
}