package io.ipoli.android.common.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 9/17/17.
 */
class LockableScrollView : ScrollView {
    var isLocked = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet)
        : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        if (isLocked) false else super.onInterceptTouchEvent(ev)

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            return if (!isLocked) super.onTouchEvent(ev) else isLocked
        }
        return super.onTouchEvent(ev)
    }
}