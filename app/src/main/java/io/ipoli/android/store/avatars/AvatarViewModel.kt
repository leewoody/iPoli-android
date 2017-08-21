package io.ipoli.android.store.avatars

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import io.ipoli.android.quest.data.Quest
import io.ipoli.android.quest.overview.ui.OverviewQuestViewModel
import io.ipoli.android.store.avatars.data.Avatar

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 8/20/17.
 */
data class AvatarViewModel(val code: Int,
                           @StringRes val name: Int,
                           val price: Int,
                           @DrawableRes val picture: Int,
                           val isBought: Boolean)