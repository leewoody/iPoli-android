package io.ipoli.android.quest.data

import io.ipoli.android.Constants
import io.ipoli.android.common.datetime.DateUtils
import io.ipoli.android.common.datetime.Time
import io.ipoli.android.common.datetime.TimePreference
import io.ipoli.android.common.persistence.PersistedModel
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Venelin Valkov <venelin></venelin>@curiousily.com>
 * on 8/19/17.
 */
open class Quest : RealmObject, PersistedModel {

    @PrimaryKey
    override var id: String = ""

    var rawText: String? = null

    var name: String = ""

    var category: String? = null

    var isAllDay: Boolean? = null
        private set

    private var priority: Int? = null

    var startMinute: Int? = null

    var preferredStartTime: String? = null

    private var duration: Int? = null

    private var createdAt: Long? = null
    private var updatedAt: Long? = null

    var start: Long? = null
    var end: Long? = null
        set(end) {
            previousScheduledDate = this.end
            field = end
        }

    var scheduled: Long? = null

    var originalScheduled: Long? = null

    var repeatingQuestId: String? = null

    var reminders: RealmList<Reminder> = RealmList()
    var subQuests: RealmList<SubQuest> = RealmList()

    var difficulty: Int? = null

    var completedAt: Long? = null
    var completedAtMinute: Int? = null

    var actualStart: Long? = null

    var challengeId: String? = null

    var coins: Long? = null
    var experience: Long? = null

    var notes: RealmList<Note> = RealmList()

    var source: String? = null

    var sourceMapping: SourceMapping? = null

    var previousScheduledDate: Long? = null

    @Transient
    var isPlaceholder: Boolean = false

    constructor()

    constructor(name: String, category: Category) : this(name, null, category) {}

    @JvmOverloads constructor(name: String, endDate: LocalDate? = null, category: Category = Category.PERSONAL) {
        this.name = name
        this.endDate = endDate
        startDate = endDate
        scheduledDate = endDate
        startMinute = null
        createdAt = DateUtils.nowUTC().getTime()
        updatedAt = DateUtils.nowUTC().getTime()
        this.category = category.name
        this.source = Constants.API_RESOURCE_SOURCE
        this.isAllDay = false
    }

    var originalScheduledDate: LocalDate?
        get() = if (originalScheduled != null) DateUtils.fromMillis(originalScheduled!!) else null
        set(originalScheduledDate) {
            originalScheduled = if (originalScheduledDate != null) DateUtils.toMillis(originalScheduledDate) else null
        }

    var scheduledDate: LocalDate?
        get() = if (scheduled != null) DateUtils.fromMillis(scheduled!!) else null
        set(scheduledDate) {
            previousScheduledDate = scheduled
            scheduled = if (scheduledDate != null) DateUtils.toMillis(scheduledDate) else null
            if (originalScheduledDate == null) {
                originalScheduledDate = scheduledDate
            }
        }

    var startTime: Time?
        get() = if (startMinute == null) {
            null
        } else Time.of(startMinute!!)
        set(time) = if (time != null) {
            startMinute = time.toMinuteOfDay()
        } else {
            startMinute = null
        }

    var categoryType: Category
        get() = Category.valueOf(category!!)
        set(category) {
            this.category = category.name
        }

    val isCompleted: Boolean
        get() = completedAtDate != null

    fun setDuration(duration: Int?) {
        if (duration == null) {
            this.duration = null
            return
        }
        this.duration = Math.min(TimeUnit.HOURS.toMinutes(Constants.MAX_QUEST_DURATION_HOURS.toLong()), duration.toLong()).toInt()
    }

    fun getDuration(): Int {
        return if (duration != null) duration!! else 0
    }

    fun setAllDay(allDay: Boolean) {
        this.isAllDay = allDay
    }

    fun getPriority(): Int {
        return if (priority != null) priority!! else Quest.PRIORITY_NOT_IMPORTANT_NOT_URGENT
    }

    fun setPriority(priority: Int?) {
        this.priority = priority
    }

    var startDate: LocalDate?
        get() = if (start != null) DateUtils.fromMillis(start!!) else null
        set(startDate) {
            if (startDate == null) {
                start = null
                return
            }
            start = DateUtils.toMillis(startDate)
        }

    var endDate: LocalDate?
        get() = if (this.end != null) DateUtils.fromMillis(this.end!!) else null
        set(endDate) {
            if (endDate == null) {
                end = null
                return
            }
            end = DateUtils.toMillis(endDate)
        }

    var actualStartDate: Date?
        get() = if (actualStart != null) Date(actualStart!!) else null
        set(actualStartDate) {
            actualStart = (if (actualStartDate != null) actualStartDate.getTime() else null)?.toLong()
        }

    var completedAtDate: LocalDate?
        get() = if (completedAt != null) DateUtils.fromMillis(completedAt!!) else null
        set(completedAtDate) {
            completedAt = if (completedAtDate == null) null else DateUtils.toMillis(completedAtDate)
        }

    val isScheduled: Boolean
        get() = scheduled != null && hasStartTime()

    val isScheduledForToday: Boolean
        get() = isScheduledFor(LocalDate.now())


    fun isScheduledFor(date: LocalDate): Boolean {
        return date.isEqual(DateUtils.fromMillis(scheduled!!))
    }

    val isScheduledForThePast: Boolean
        get() = scheduled != null && scheduledDate!!.isBefore(LocalDate.now())

    val isStarted: Boolean
        get() = actualStart != null && completedAt == null

    val isFromRepeatingQuest: Boolean
        get() = !repeatingQuestId.isNullOrEmpty()

    val isFromChallenge: Boolean
        get() = !challengeId.isNullOrEmpty()

    val actualDuration: Int
        get() {
            if (this.isCompleted && actualStartDate != null) {
                val startMinute = actualLocalStartMinute
                val minutes = completedAtMinute!! - startMinute
                return if (minutes >= 0) minutes else Time.MINUTES_IN_A_DAY - startMinute + completedAtMinute!!
            }
            return getDuration()
        }

    private val actualLocalStartMinute: Int
        get() {
            val localActualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(actualStart!!), ZoneId.systemDefault())
            return Math.max(0, localActualStart.hour * 60 + localActualStart.minute)
        }

    val actualStartMinute: Int?
        get() = if (this.isCompleted && actualStartDate != null) {
            if (DateUtils.fromMillis(actualStart!!).equals(DateUtils.fromMillis(completedAt!!))) {
                actualLocalStartMinute
            } else {
                0
            }
        } else startMinute

    val textNotes: List<Note>
        get() {
            val textNotes: MutableList<Note> = ArrayList()
            for (note in notes) {
                if (note.noteType.equals(Note.NoteType.TEXT.name)) {
                    textNotes.add(note)
                }
            }
            return textNotes
        }

    fun addNote(note: Note) {
        notes.add(note)
    }


    fun removeTextNote() {
        val txtNotes = textNotes
        notes.removeAll(txtNotes)
    }


    fun addSubQuest(subQuest: SubQuest) {
        subQuests.add(subQuest)
    }


    fun removeSubQuest(subQuest: SubQuest) {
        subQuests.remove(subQuest)
    }


    fun hasStartTime(): Boolean {
        return startMinute != null
    }

    fun addReminder(reminder: Reminder) {
        reminders.add(reminder)
    }

    var startTimePreference: TimePreference?
        get() = if (preferredStartTime.isNullOrEmpty()) {
            TimePreference.ANY
        } else TimePreference.valueOf(preferredStartTime!!)
        set(timePreference) {
            if (timePreference != null) {
                this.preferredStartTime = timePreference.name
            }
        }


    fun getStartDateTimeMillis(): Long? {
        if (startMinute == null || scheduled == null) {
            return null
        }
        val startTime = Time.of(startMinute!!)
        return DateUtils.toStartOfDay(scheduledDate!!).getTime() + startTime.toMillisOfDay()
    }

    companion object {

        val PRIORITY_MOST_IMPORTANT_FOR_DAY = 4
        val PRIORITY_IMPORTANT_URGENT = 3
        val PRIORITY_IMPORTANT_NOT_URGENT = 2
        val PRIORITY_NOT_IMPORTANT_URGENT = 1
        val PRIORITY_NOT_IMPORTANT_NOT_URGENT = 0

        fun isStarted(quest: Quest): Boolean {
            return quest.actualStartDate != null && quest.completedAtDate == null
        }
    }
}
