package io.ipoli.android.quest.data

import io.realm.RealmObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 8/19/17.
 */
open class Reminder : RealmObject {

    var message: String? = null

    private var minutesFromStart: Long? = null

    var notificationId: String? = null

    var start: Long? = null

    constructor()

    @JvmOverloads constructor(minutesFromStart: Long, notificationId: String = Random().nextInt().toString()) {
        this.minutesFromStart = minutesFromStart
        this.notificationId = notificationId
    }

    fun getMinutesFromStart(): Long {
        return minutesFromStart!!
    }

    fun setMinutesFromStart(minutesFromStart: Long) {
        this.minutesFromStart = minutesFromStart
    }

    val notificationNum: Int
        get() = Integer.valueOf(notificationId)!!

    fun calculateStartTime(quest: Quest) {
        val questStartTime = quest.getStartDateTimeMillis()
        if (questStartTime == null) {
            start = null
            return
        }
        start = questStartTime + TimeUnit.MINUTES.toMillis(getMinutesFromStart())
    }

    var startTime: Date?
        get() = if (start != null) Date(start!!) else null
        set(startTime) {
            start = startTime?.time
        }
}
