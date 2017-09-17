package io.ipoli.android.quest.calendar.ui.dayview

import android.content.Context
import android.content.res.Resources
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import io.ipoli.android.R
import io.ipoli.android.common.datetime.Time
import kotlinx.android.synthetic.main.calendar_hour_cell.view.*
import kotlinx.android.synthetic.main.view_calendar_day.view.*
import kotlin.reflect.KClass

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 9/2/17.
 */

interface StateChangeListener {
    fun onStateChanged(state: CalendarDayView.State)
}

class CalendarDayView : FrameLayout, StateChangeListener {

    class FSM(initialState: State, private val listener: StateChangeListener) {

        interface Action<in E : Event> {
            fun execute(state: State, event: E): State
        }

        private var currentState: State = initialState
        private val actions = mutableMapOf<Pair<State.Type, KClass<*>>, Action<*>>()

        init {
            listener.onStateChanged(currentState)
        }

        fun <E : Event> transition(given: State.Type, on: KClass<E>, execute: (state: State, event: E) -> State) {
            val a = object : Action<E> {
                override fun execute(state: State, event: E): State {
                    return execute(state, event)
                }
            }
            actions[Pair(given, on)] = a
        }

        fun <E : Event> fire(event: E) {
            val actionKey = Pair(currentState.type, event::class)
            @Suppress("UNCHECKED_CAST")
            val a = actions[actionKey] as Action<E>
            currentState = a.execute(currentState, event)
            listener.onStateChanged(currentState)
        }
    }

    sealed class Event {
        object CompleteEdit : Event()
        object Up : Event()
        data class StartEdit(val y: Float, val name: String) : Event()
        data class Drag(val y: Float) : Event()
        data class DragTop(val y: Float) : Event()
        data class DragBottom(val y: Float) : Event()
        data class EditName(val name: String) : Event()
        object CompleteEditName : Event()
    }

    data class State(
        val type: State.Type,
        val topPosition: Float? = null,
        val topDragViewPosition: Float? = null,
        val bottomDragViewPosition: Float? = null,
        val height: Int? = null,
        val name: String? = null) {
        enum class Type {
            // DRAG_TOP, DRAG_BOTTOM,
            VIEW,
            EDIT, DRAG, EDIT_NAME
        }
    }

    private var dragView: View? = null
    private var lastY: Float? = null

    private lateinit var fsm: FSM

    private val MIN_EVENT_DURATION = 10
    private val MAX_EVENT_DURATION = Time.h2Min(4)
    private var hourHeight: Float = 0f
    private var minuteHeight: Float = 0f
    private lateinit var dragImage: Drawable
    private var dragImageSize: Int = toPx(16)
    private val adapterViews = mutableListOf<View>()

    private lateinit var editModeBackground: View
    private lateinit var topDragView: View
    private lateinit var bottomDragView: View
    private lateinit var positionToTimeMapper: PositionToTimeMapper

    private var scheduledEventsAdapter: ScheduledEventsAdapter<*>? = null
    private var unscheduledEventsAdapter: UnscheduledEventsAdapter<*>? = null

    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            refreshEventsFromAdapter()
        }

        override fun onInvalidated() {
            removeAllViews()
        }
    }

    constructor(context: Context) : super(context) {
        initUi(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initUi(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initUi(attrs, defStyleAttr)
    }

    private fun initUi(attrs: AttributeSet?, defStyleAttr: Int) {
        setMainLayout()
        fetchStyleAttributes(attrs, defStyleAttr)
        val screenHeight = getScreenHeight()
        hourHeight = screenHeight / 6f
        minuteHeight = hourHeight / 60f

        positionToTimeMapper = PositionToTimeMapper(minuteHeight)

        setupScroll()
        setupHourCells()
        setupEditBackgroundView()
        setupUnscheduledQuests()

        topDragView = addDragView()
        bottomDragView = addDragView()

        setupFSM()
    }

    private fun setupFSM() {
        fsm = FSM(State(State.Type.VIEW), this)
        fsm.transition(State.Type.VIEW, Event.StartEdit::class, { s, e ->

            val absPos = e.y - topLocationOnScreen
            val topPosition = roundPositionToMinutes(absPos)

            s.copy(
                type = State.Type.DRAG,
                topPosition = topPosition,
                topDragViewPosition = topPosition - dragImageSize / 2,
                bottomDragViewPosition = topPosition + dragView!!.height - dragImageSize / 2)

//            s.copy(type = State.Type.DRAG, topPosition = e.y, height = e.height, name = e.name)
        })

        fsm.transition(State.Type.DRAG, Event.Drag::class, { s, e ->
            //                    val yPosition = e.rawY - topLocationOnScreen - initialOffset
            val absPos = e.y - topLocationOnScreen
            val topPosition = roundPositionToMinutes(absPos)

            s.copy(
                topPosition = topPosition,
                topDragViewPosition = topPosition - dragImageSize / 2,
                bottomDragViewPosition = topPosition + dragView!!.height - dragImageSize / 2)
        })

        fsm.transition(State.Type.DRAG, Event.Up::class, { s, _ ->
            s.copy(type = State.Type.EDIT)
        })

        fsm.transition(State.Type.DRAG, Event.DragTop::class, { s, e ->
            s.copy(topPosition = e.y)
        })

        fsm.transition(State.Type.DRAG, Event.DragBottom::class, { s, e ->
            s.copy(topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT, Event.Drag::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT, Event.DragTop::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT, Event.DragBottom::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT, Event.EditName::class, { s, e ->
            s.copy(type = State.Type.EDIT_NAME, name = e.name)
        })

        fsm.transition(State.Type.EDIT, Event.CompleteEdit::class, { s, e ->
            s.copy(type = State.Type.VIEW)
        })

        fsm.transition(State.Type.EDIT_NAME, Event.CompleteEditName::class, { s, e ->
            s.copy(type = State.Type.EDIT)
        })

        fsm.transition(State.Type.EDIT_NAME, Event.EditName::class, { s, e ->
            s.copy(type = State.Type.EDIT_NAME, name = e.name)
        })

        fsm.transition(State.Type.EDIT_NAME, Event.Drag::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT_NAME, Event.DragTop::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })

        fsm.transition(State.Type.EDIT_NAME, Event.DragBottom::class, { s, e ->
            s.copy(type = State.Type.DRAG, topPosition = e.y)
        })
    }

    private fun setupScroll() {
        scrollView.isVerticalScrollBarEnabled = false
    }

    private fun setMainLayout() {
        LayoutInflater.from(context).inflate(R.layout.view_calendar_day, this, true)
    }

    private fun fetchStyleAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarDayView, defStyleAttr, 0)
            dragImage = a.getDrawable(R.styleable.CalendarDayView_dragImage)
            dragImageSize = a.getDimensionPixelSize(R.styleable.CalendarDayView_dragImageSize, dragImageSize)
            a.recycle()
        }
    }

    private fun setupEditBackgroundView() {
        editModeBackground = View(context)
        editModeBackground.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        editModeBackground.setBackgroundResource(R.color.md_dark_text_12)
        editModeBackground.visibility = View.GONE
        addView(editModeBackground)
    }

    private fun setupHourCells() {
        for (hour in 0..23) {
            val hourView = LayoutInflater.from(context).inflate(R.layout.calendar_hour_cell, this, false)
            if (hour > 0) {
                hourView.timeLabel.text = hour.toString() + ":00"
            }
            val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, hourHeight.toInt())
            layoutParams.topMargin = (hour * hourHeight).toInt()
            hourView.layoutParams = layoutParams
            eventContainer.addView(hourView)
        }
    }

    private fun addDragView(): View {
        val view = ImageView(context)
        view.layoutParams = LayoutParams(dragImageSize, dragImageSize)
        view.setImageDrawable(dragImage)
        view.visibility = View.GONE
        addView(view)
        return view
    }

    private fun setupUnscheduledQuests() {
        unscheduledQuests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        unscheduledQuests.isNestedScrollingEnabled = false
    }

    fun setUnscheduledQuestsAdapter(adapter: UnscheduledEventsAdapter<*>) {
        unscheduledEventsAdapter = adapter
        unscheduledQuests.adapter = adapter
    }

    fun setScheduledEventsAdapter(adapter: ScheduledEventsAdapter<*>) {
        this.scheduledEventsAdapter?.unregisterDataSetObserver(dataSetObserver)
        this.scheduledEventsAdapter = adapter
        this.scheduledEventsAdapter?.registerDataSetObserver(dataSetObserver)
        addEventsFromAdapter()
    }

    private fun addEventsFromAdapter() {
//        removeAllViews()
        val a = scheduledEventsAdapter!!
        for (i in 0 until a.count) {
            val adapterView = a.getView(i, null, eventContainer)
            val event = a.getItem(i)
            adapterView.setPositionAndHeight(
                event.startMinute * minuteHeight,
                (event.duration * minuteHeight).toInt())
            adapterViews.add(i, adapterView)
            eventContainer.addView(adapterView)
        }
    }

    private fun refreshEventsFromAdapter() {
        val a = scheduledEventsAdapter!!
        val eventsInViewCount = childCount
        val eventsInAdapterCount = a.count
        val reuseCount = Math.min(eventsInViewCount, eventsInAdapterCount)

        for (i in 0 until reuseCount) {
            a.getView(i, getChildAt(i), eventContainer)
        }

        if (eventsInViewCount < eventsInAdapterCount) {
            for (i in eventsInViewCount until eventsInAdapterCount) {
                eventContainer.addView(a.getView(i, null, eventContainer), i)
            }
        } else if (eventsInViewCount > eventsInAdapterCount) {
            removeViews(eventsInAdapterCount, eventsInViewCount)
        }
    }

    fun scheduleEvent(adapterView: View) {
        scrollView.isLocked = true
        val dragView = addAndPositionDragView(adapterView)
        dragView.post {
            this.dragView = dragView
            // @TODO get event name
//            fsm.fire(Event.StartEdit(dragView.top.toFloat(), dragView.height, "namy"))
            fsm.fire(Event.StartEdit(dragView.topLocationOnScreen.toFloat(), "namy"))
            setupDragViews(dragView)
            editModeBackground.bringToFront()
            showViews(editModeBackground, topDragView, bottomDragView)

            val initialOffset = lastY!! - dragView.topLocationOnScreen

            adapterView.setOnTouchListener { _, e ->

                setBackgroundTouchListener()
                val action = e.actionMasked

                if (action == MotionEvent.ACTION_MOVE) {
//                    val yPosition = e.rawY - topLocationOnScreen - initialOffset
                    fsm.fire(Event.Drag(e.rawY - initialOffset))
                }

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    fsm.fire(Event.Up)
                    setOnTouchListener(null)
                    setDragViewTouchListener()
                    setTopDragViewListener()
                    setBottomDragViewListener()
                    adapterView.setOnTouchListener(null)
                }
                true
            }
            scheduledEventsAdapter?.onStartEdit(adapterView)

        }
    }

    private fun setBackgroundTouchListener() {
        editModeBackground.setOnTouchListener { _, ev ->
            val action = ev.actionMasked
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                fsm.fire(Event.CompleteEdit)
            }
            true
        }
    }

    private fun addAndPositionDragView(adapterView: View): View {
        TransitionManager.beginDelayedTransition(this)
        val dragView = LayoutInflater.from(context).inflate(R.layout.item_calendar_drag, this, false)
        dragView.setPositionAndHeight(
            adapterView.topLocationOnScreen - topLocationOnScreen.toFloat(),
            adapterView.height
        )
        addView(dragView)
        return dragView
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        lastY = ev.rawY
        return super.dispatchTouchEvent(ev)
    }

    private fun setupDragViews(dragView: View) {
        setupTopDragView(dragView)
        setupBottomDragView(dragView)
    }

    private fun setupBottomDragView(editView: View) {
        bottomDragView.elevation = editView.elevation
        bottomDragView.bringToFront()
        positionBottomDragView(editView)
        setBottomDragViewListener()
    }

    private fun setupTopDragView(editView: View) {
        topDragView.elevation = editView.elevation
        topDragView.bringToFront()
        positionTopDragView(editView)
        setTopDragViewListener()
    }

    private fun positionBottomDragView(editView: View) {
        val lp = bottomDragView.layoutParams as MarginLayoutParams
        lp.topMargin = editView.bottom - dragImageSize / 2
        lp.marginStart = editView.left + editView.width / 2 - dragImageSize / 2
        bottomDragView.layoutParams = lp
    }

    private fun positionTopDragView(editView: View) {
        val lp = topDragView.layoutParams as MarginLayoutParams
        lp.topMargin = editView.top - dragImageSize / 2
        lp.marginStart = editView.left + editView.width / 2 - dragImageSize / 2
        topDragView.layoutParams = lp
    }

    private fun setDragViewTouchListener() {
        var initialOffset = lastY!! - dragView!!.topLocationOnScreen
        dragView!!.setOnTouchListener { _, e ->
            val action = e.actionMasked
            if (action == MotionEvent.ACTION_DOWN) {
                initialOffset = e.rawY - dragView!!.topLocationOnScreen
            }
            if (action == MotionEvent.ACTION_MOVE) {
//                fsm.fire(Event.Drag((e.rawY - topLocationOnScreen - initialOffset).toInt(), dragView!!.height))
                fsm.fire(Event.Drag(e.rawY - initialOffset))
            }
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                fsm.fire(Event.Up)
            }
            true
        }
    }

    private fun setBottomDragViewListener() {
        bottomDragView.setOnTouchListener { _, e ->
            val action = e.actionMasked
            if (action == MotionEvent.ACTION_MOVE) {
//                fsm.fire(Event.DragBottom((e.rawY - topLocationOnScreen).toInt(), dragView!!.height))
                fsm.fire(Event.DragBottom(e.rawY))
            }
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                fsm.fire(Event.Up)
            }
            true
        }
    }

    private fun setTopDragViewListener() {
        topDragView.setOnTouchListener { _, e ->
            val action = e.actionMasked
            if (action == MotionEvent.ACTION_MOVE) {
                fsm.fire(Event.DragTop(e.rawY))
            }
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                fsm.fire(Event.Up)
            }
            true
        }
    }

    override fun onStateChanged(state: State) {
        when (state.type) {
            State.Type.DRAG -> {
//                val topPosition = roundPositionToMinutes(state.topPosition!!)
                dragView?.setTopPosition(state.topPosition!!)
                topDragView.setTopPosition(state.topDragViewPosition!!)
//                bottomDragView.setTopPosition(topPosition + state.height!!.toFloat() - dragImageSize / 2)
                bottomDragView.setTopPosition(state.bottomDragViewPosition!!)
            }

//            State.Type.DRAG_TOP -> {
//                val topPosition = roundPositionToMinutes(state.topPosition!!)
//                val dy = topPosition - dragView!!.top
//                val dragHeight = dragView!!.height - dy.toInt()
//                if (isValidHeightForEvent(dragHeight)) {
//                    dragView?.changePositionAndHeight(dy, dragHeight)
//                    topDragView.setTopPosition(topPosition - dragImageSize / 2)
//                }
//            }
//
//            State.Type.DRAG_BOTTOM -> {
//                val topPosition = roundPositionToMinutes(state.topPosition!!)
//                val dragHeight = (topPosition - dragView!!.top).toInt()
//                if (isValidHeightForEvent(dragHeight)) {
//                    dragView?.changeHeight(dragHeight)
//                    bottomDragView.setTopPosition(topPosition - dragImageSize / 2)
//                }
//            }

            State.Type.VIEW -> {
                TransitionManager.beginDelayedTransition(this)
                hideViews(editModeBackground, topDragView, bottomDragView)
                scheduledEventsAdapter?.onStopEdit(dragView!!)
                removeView(dragView)
                dragView = null
            }
        }
    }

    private fun roundPositionToMinutes(position: Int, roundedToMinutes: Int = 5) =
        roundPositionToMinutes(position.toFloat(), roundedToMinutes)

    private fun roundPositionToMinutes(position: Float, roundedToMinutes: Int = 5) =
        positionToTimeMapper.timeAt(position, roundedToMinutes).toPosition()

    private fun View.changePositionAndHeight(yDelta: Float, height: Int) =
        changeLayoutParams<MarginLayoutParams> {
            it.topMargin += yDelta.toInt()
            it.height = height
        }

    private fun View.setPositionAndHeight(yPosition: Float, height: Int) =
        changeLayoutParams<MarginLayoutParams> {
            it.topMargin = yPosition.toInt()
            it.height = height
        }

    private fun View.setTopPosition(yPosition: Float) =
        changeLayoutParams<MarginLayoutParams> { it.topMargin = yPosition.toInt() }

    private fun View.changePosition(yDelta: Float) =
        changePosition(yDelta.toInt())

    private fun View.changePosition(yDelta: Int) =
        changeLayoutParams<MarginLayoutParams> { it.topMargin += yDelta }

    private fun View.changeHeight(height: Int) =
        changeLayoutParams<MarginLayoutParams> { it.height = height }

    private fun <T : ViewGroup.LayoutParams> View.changeLayoutParams(cb: (layoutParams: T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val lp = layoutParams as T
        cb(lp)
        layoutParams = lp
    }

    private val View.topLocationOnScreen: Int
        get() {
            val location = IntArray(2)
            getLocationOnScreen(location)
            return location[1]
        }

    private fun Time.toPosition() =
        toMinuteOfDay() * minuteHeight

    private fun isValidHeightForEvent(height: Int): Boolean =
        getMinutesFor(height) in MIN_EVENT_DURATION..MAX_EVENT_DURATION

    private fun getScreenHeight(): Int {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }

    private fun getMinutesFor(height: Int): Int =
        (height / minuteHeight).toInt()

    private fun showViews(vararg views: View) =
        views.forEach { it.visibility = View.VISIBLE }

    private fun hideViews(vararg views: View) =
        views.forEach { it.visibility = View.GONE }

    private fun toPx(dp: Int): Int =
        (dp * Resources.getSystem().displayMetrics.density).toInt()
}