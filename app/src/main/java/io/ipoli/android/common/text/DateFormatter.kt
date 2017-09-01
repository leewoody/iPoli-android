package io.ipoli.android.common.text

import android.content.Context
import io.ipoli.android.R
import io.ipoli.android.common.datetime.DateUtils
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 8/22/17.
 */
object DateFormatter {
    private val DEFAULT_EMPTY_VALUE = "Don't know"
    private val DEFAULT_DATE_FORMAT = SimpleDateFormat("dd MMM yy", Locale.getDefault())
    private val DATE_NO_YEAR_FORMAT = SimpleDateFormat("dd MMM", Locale.getDefault())

    fun format(context: Context, date: LocalDate?): String {
        if (date == null) {
            return DEFAULT_EMPTY_VALUE
        }
        if (DateUtils.isToday(date)) {
            return context.getString(R.string.today)
        }
        return if (DateUtils.isTomorrow(date)) {
            context.getString(R.string.tomorrow)
        } else DEFAULT_DATE_FORMAT.format(DateUtils.toStartOfDay(date))
    }

    @JvmOverloads
    fun formatWithoutYear(context: Context, date: LocalDate?, emptyValue: String = DEFAULT_EMPTY_VALUE, currentDate: LocalDate? = null): String {
        if (date == null) {
            return emptyValue
        }
        if (DateUtils.isToday(date)) {
            return context.getString(R.string.today)
        }
        if (DateUtils.isTomorrow(date)) {
            return context.getString(R.string.tomorrow)
        }
        if (DateUtils.isYesterday(date)) {
            return context.getString(R.string.yesterday)
        }
        if (currentDate != null) {
            if (currentDate.with(DayOfWeek.MONDAY).isEqual(date.with(DayOfWeek.MONDAY))) {
                return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
            }
        }
        return DATE_NO_YEAR_FORMAT.format(DateUtils.toStartOfDay(date))
    }

    fun formatWithoutYear(context: Context, date: LocalDate, currentDate: LocalDate): String {
        return formatWithoutYear(context, date, DEFAULT_EMPTY_VALUE, currentDate)
    }

    fun formatWithoutYearSimple(date: LocalDate): String {
        return DATE_NO_YEAR_FORMAT.format(DateUtils.toStartOfDay(date))
    }
}