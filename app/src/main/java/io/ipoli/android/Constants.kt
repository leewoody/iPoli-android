package io.ipoli.android

import org.threeten.bp.DayOfWeek
import java.util.*

/**
 * Created by Venelin Valkov venelin@curiousily.com>
 * on 8/20/17.
 */
interface Constants {
    companion object {

        val FACEBOOK_APP_LINK = "https://fb.me/1609677589354576"
        val IPOLI_LOGO_URL = "https://i.imgur.com/Gz3rUi1.png"
        val INVITE_IMAGE_URL = "https://i.imgur.com/fLToavB.png"
        val SHARE_URL = "http://bit.ly/ipoli-android"
        val TWITTER_USERNAME = "@iPoliHQ"

        val REMIND_DAILY_CHALLENGE_NOTIFICATION_ID = 101
        val ONGOING_NOTIFICATION_ID = 102
        val MEMBERSHIP_EXPIRATION_NOTIFICATION_ID = 103

        val QUEST_TIMER_NOTIFICATION_ID = 201
        val QUEST_COMPLETE_NOTIFICATION_ID = 202

        val PET_STATE_CHANGED_NOTIFICATION_ID = 301

        val DEFAULT_SNOOZE_TIME_MINUTES = 10

        val QUEST_WITH_NO_DURATION_TIMER_MINUTES = 30
        val MAX_QUEST_DURATION_HOURS = 4

        val PLAYER_ID_EXTRA_KEY = "player_id"

        val PROFILE_ID_EXTRA_KEY = "profile_id"

        val QUEST_ID_EXTRA_KEY = "quest_id"

        val REPEATING_QUEST_ID_EXTRA_KEY = "repeating_quest_id"

        val CHALLENGE_ID_EXTRA_KEY = "challenge_id"

        val REWARD_ID_EXTRA_KEY = "reward_id"

        val CURRENT_SELECTED_DAY_EXTRA_KEY = "CURRENT_SELECTED_DAY"

        val DISPLAY_NAME_EXTRA_KEY = "display_name"

        val SHOW_TRIAL_MESSAGE_EXTRA_KEY = "show_trial_message"

        val CALENDAR_EVENT_MIN_DURATION = 15

        val CALENDAR_EVENT_MIN_SINGLE_LINE_DURATION = 20

        val CALENDAR_EVENT_MIN_TWO_LINES_DURATION = 30

        val QUEST_MIN_DURATION = 10

        val MAX_UNSCHEDULED_QUEST_VISIBLE_COUNT = 3
        val RESULT_REMOVED = 100

        val KEY_APP_RUN_COUNT = "APP_RUN_COUNT"

        val KEY_APP_VERSION_CODE = "APP_VERSION_CODE"

        val KEY_PLAYER_ID = "PLAYER_ID"

        val KEY_DAILY_CHALLENGE_DAYS = "DAILY_CHALLENGE_DAYS"

        val KEY_DAILY_CHALLENGE_REMINDER_START_MINUTE = "DAILY_CHALLENGE_REMINDER_START_MINUTE"

        val KEY_DAILY_CHALLENGE_ENABLE_REMINDER = "DAILY_CHALLENGE_ENABLE_REMINDER"

        val KEY_DAILY_CHALLENGE_LAST_COMPLETED = "DAILY_CHALLENGE_LAST_COMPLETED"

        val KEY_ONGOING_NOTIFICATION_ENABLED = "ONGOING_NOTIFICATION_ENABLED"

        val KEY_SHOULD_SHOW_TUTORIAL = "SHOULD_SHOW_TUTORIAL"

        val IPOLI_EMAIL = "hi@ipoli.io"

        val API_RESOURCE_SOURCE = "ipoli-android"
        val DEFAULT_PLAYER_XP = 20
        val DEFAULT_PLAYER_LEVEL = 1
        val DEFAULT_PLAYER_COINS: Long = 10
        //        val DEFAULT_PLAYER_AVATAR = Avatar.IPOLI_CLASSIC
//        val DEFAULT_PLAYER_PRODUCTIVE_TIMES: Set<TimeOfDay> = HashSet<Any>(listOf(TimeOfDay.MORNING))
        val DEFAULT_PLAYER_WORK_START_MINUTE = 10 * 60
        val DEFAULT_PLAYER_WORK_END_MINUTE = 18 * 60
        val DEFAULT_PLAYER_SLEEP_START_MINUTE = 23 * 60
        val DEFAULT_PLAYER_SLEEP_END_MINUTE = 8 * 60
        val DEFAULT_PLAYER_COMPLETE_DAILY_QUESTS_MINUTE = 0

        val DEFAULT_PLAYER_WORK_DAYS: Set<Int> = HashSet<Int>(Arrays.asList(
            DayOfWeek.MONDAY.getValue(),
            DayOfWeek.TUESDAY.getValue(),
            DayOfWeek.WEDNESDAY.getValue(),
            DayOfWeek.THURSDAY.getValue(),
            DayOfWeek.FRIDAY.getValue()
        ))

        val DURATIONS = arrayOf(10, 15, 25, 30, 45, 60, 90, 120)

        val REWARD_COINS = arrayOf(10, 20, 50, 100, 200, 500, 1000)

        val DEFAULT_REWARD_PRICE = 10

        val REWARD_MAX_PRICE = 10000

        val REWARD_MIN_PRICE = 1

        val DEFAULT_DAILY_CHALLENGE_REMINDER_START_MINUTE = 10 * 60

        val DEFAULT_DAILY_CHALLENGE_ENABLE_REMINDER = true

        val DEFAULT_ONGOING_NOTIFICATION_ENABLED = true
        val REMINDER_PREDEFINED_MINUTES = intArrayOf(0, 10, 15, 30, 60)
        val MIN_FLEXIBLE_TIMES_A_WEEK_COUNT = 1
        val MAX_FLEXIBLE_TIMES_A_WEEK_COUNT = 6

        val MIN_FLEXIBLE_TIMES_A_MONTH_COUNT = 1
        val MAX_FLEXIBLE_TIMES_A_MONTH_COUNT = 15

        val DEFAULT_DAILY_CHALLENGE_DAYS: Set<Int> = HashSet<Int>(Arrays.asList(
            DayOfWeek.MONDAY.getValue(),
            DayOfWeek.TUESDAY.getValue(),
            DayOfWeek.WEDNESDAY.getValue(),
            DayOfWeek.THURSDAY.getValue(),
            DayOfWeek.FRIDAY.getValue()
        ))

        val DAILY_CHALLENGE_QUEST_COUNT = 3
        val DEFAULT_CHALLENGE_DEADLINE_DAY_DURATION = 30
        val DEFAULT_BAR_COUNT = 4
        val REMINDER_START_TIME = "reminder_start_time"

        val QUICK_ADD_ADDITIONAL_TEXT = "quick_add_additional_text"
        val DEFAULT_PET_NAME = "Flopsy"
        //        val DEFAULT_PET_AVATAR = PetAvatar.ELEPHANT
        val DEFAULT_PET_BACKGROUND_PICTURE = "pet_background_1"

        val DEFAULT_PET_HP = 80
        val XP_BONUS_PERCENTAGE_OF_HP = 20.0
        val COINS_BONUS_PERCENTAGE_OF_HP = 10.0
        val REWARD_POINTS_BONUS_PERCENTAGE_OF_HP = 10.0
        val MAX_PET_COIN_BONUS = 10
        val MAX_PET_REWARD_POINTS_BONUS = 10

        val MAX_PET_XP_BONUS = 20

        val XP_TO_PET_HP_RATIO = 13.2
        val REVIVE_PET_COST = 300
        val PREDEFINED_CHALLENGE_INDEX = "predefined_challenge_index"
        val RANDOM_SEED = 42 // duh!
        val MAX_TIMES_A_DAY_COUNT = 8
        val SCHEMA_VERSION = 9

        val MAX_PENALTY_COEFFICIENT = 0.5
        val NO_QUESTS_PENALTY_COEFFICIENT = 0.3
        val IMPORTANT_QUEST_PENALTY_PERCENT = 5.0

        val KEY_WIDGET_AGENDA_QUEST_LIST = "widget_agenda_quest_list"
        val API_READ_TIMEOUT_SECONDS = 30
        val DEFAULT_VIEW_VERSION = "1.0"
        val SOURCE_ANDROID_CALENDAR = "android-calendar"

        val XP_BAR_MAX_VALUE = 100
        val RC_CALENDAR_PERM = 102
        val KEY_LAST_ANDROID_CALENDAR_SYNC_DATE = "LAST_ANDROID_CALENDAR_SYNC_DATE"
        val FACEBOOK_PACKAGE = "com.facebook.katana"
        val TWITTER_PACKAGE = "com.twitter.android"
        val SYNC_CALENDAR_JOB_ID = 1
        val PROFILES_FIRST_SCHEMA_VERSION = 7

        val POWER_UP_GRACE_PERIOD_DAYS = 7
        val POWER_UPS_TRIAL_PERIOD_DAYS = 15

        val SKU_SUBSCRIPTION_MONTHLY = "monthly_plan_70_percent"
        val SKU_SUBSCRIPTION_QUARTERLY = "quarterly_plan_70_percent"
        val SKU_SUBSCRIPTION_YEARLY = "yearly_plan_70_percent"

        val KEY_ACHIEVEMENT_ACTION = "achievement_action"
        val KEY_ACHIEVEMENT_ACTION_CLASS = "achievement_action_class"
    }
}
