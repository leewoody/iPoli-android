package io.ipoli.android.quest.calendar.ui

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import io.ipoli.android.R
import io.ipoli.android.common.ViewUtils
import io.ipoli.android.mcalendar.CellConfig
import io.ipoli.android.mcalendar.listeners.OnDateClickListener
import io.ipoli.android.mcalendar.listeners.OnMonthScrollListener
import io.ipoli.android.mcalendar.vo.DateData
import io.ipoli.android.quest.calendar.DayViewController
import kotlinx.android.synthetic.main.controller_calendar.view.*
import kotlinx.android.synthetic.main.controller_calendar_toolbar.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 9/8/17.
 */
class CalendarController : Controller() {

    val MID_POSITION = 49
    val MAX_VISIBLE_DAYS = 100

    private var pickerState = 0

    private var currentMidDate = LocalDate.now()

    private val pagerAdapter = object : RouterPagerAdapter(this) {
        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val plusDays = position - MID_POSITION

                val page = DayViewController()
                router.setRoot(RouterTransaction.with(page))
            }
        }

        override fun getCount(): Int = MAX_VISIBLE_DAYS

        override fun getItemPosition(item: Any?): Int =
            PagerAdapter.POSITION_NONE
    }

    private fun changeCurrentDay(date: LocalDate) {
        currentMidDate = date
        pagerAdapter.notifyDataSetChanged()
        view!!.pager.setCurrentItem(MID_POSITION, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_calendar, container, false)

        view.pager.adapter = pagerAdapter
        view.pager.currentItem = MID_POSITION

        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        val calendarToolbar = inflater.inflate(R.layout.controller_calendar_toolbar, toolbar, false) as ViewGroup
        calendarToolbar.day.text = "Today"
        calendarToolbar.date.text = "Sept 8th 17"
        toolbar.addView(calendarToolbar)

        initDayPicker(view, calendarToolbar)

        view.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {

//                view.calendar.visibility = if (Random().nextDouble() > 0.5) View.GONE else View.VISIBLE
            }
        })

        return view
    }

    private fun initDayPicker(view: View, calendarToolbar: ViewGroup) {
        val monthPattern = DateTimeFormatter.ofPattern("MMMM")
        view.dayPickerContainer.visibility = View.GONE
        val dayPicker = view.dayPicker
        val calendarIndicator = calendarToolbar.calendarIndicator

        var currentDate = LocalDate.now()
        dayPicker.markDate(DateData(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth))
        var isOpen = false

        calendarToolbar.setOnClickListener {
            calendarIndicator.animate().rotationBy(180f).setDuration(200)
            view.currentMonth.text = LocalDate.now().format(monthPattern)

            val layoutParams = view.pager.layoutParams as ViewGroup.MarginLayoutParams
            if (!isOpen) {
                CellConfig.Month2WeekPos = CellConfig.middlePosition
                CellConfig.ifMonth = false
                dayPicker.shrink()
                isOpen = true
                layoutParams.topMargin = ViewUtils.dpToPx(-12f, view.context).toInt()
                view.pager.layoutParams = layoutParams
                view.dayPickerContainer.visibility = View.VISIBLE
            } else {
                view.dayPickerContainer.visibility = View.GONE
                isOpen = false
                layoutParams.topMargin = 0
            }
            view.pager.layoutParams = layoutParams
        }

        view.expander.setOnClickListener({

            if (CellConfig.ifMonth) {
                CellConfig.Month2WeekPos = CellConfig.middlePosition
                CellConfig.ifMonth = false
                CellConfig.weekAnchorPointDate = DateData(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth)
                dayPicker.shrink()
            } else {
                CellConfig.ifMonth = true
                CellConfig.Week2MonthPos = CellConfig.middlePosition
                dayPicker.expand()
            }
        })


        dayPicker.setOnDateClickListener(object : OnDateClickListener() {
            override fun onDateClick(v: View, date: DateData) {
                dayPicker.markedDates.removeAdd()
                dayPicker.markDate(date)
                currentDate = LocalDate.of(date.year, date.month, date.day)
                view.currentMonth.text = currentDate.format(monthPattern)
            }
        })

        dayPicker.setOnMonthScrollListener(object : OnMonthScrollListener() {
            override fun onMonthChange(year: Int, month: Int) {
                val localDate = LocalDate.of(year, month, 1)
                view.currentMonth.text = localDate.format(monthPattern)
            }

            override fun onMonthScroll(positionOffset: Float) {
            }

        })
    }

    override fun onDestroyView(view: View) {
        if (!activity!!.isChangingConfigurations) {
            view.pager.adapter = null
        }
        super.onDestroyView(view)
    }
}