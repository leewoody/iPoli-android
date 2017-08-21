package io.ipoli.android.store.avatars.data

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import io.ipoli.android.R


/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 8/21/17.
 */
enum class Avatar private constructor(val code: Int,
                                      val price: Int,
                                      @param:StringRes val avatarName: Int,
                                      @param:DrawableRes val picture: Int) {

    IPOLI_CLASSIC(1, 0, R.string.pet_donkey, R.drawable.avatar_12),
    IPOLI_YELLOW_GLASSES(2, 0, R.string.pet_donkey, R.drawable.avatar_11),
    MACARENA(3, 20, R.string.pet_donkey, R.drawable.avatar_10),
    MACARENA_MEXICAN(4, 20, R.string.pet_donkey, R.drawable.avatar_09),
    BLONDY(5, 20, R.string.pet_donkey, R.drawable.avatar_08),
    GREEN_EYES(6, 20, R.string.pet_donkey, R.drawable.avatar_07),
    PIPILOTA(7, 20, R.string.pet_donkey, R.drawable.avatar_06),
    OLD_PIRATE(8, 20, R.string.pet_donkey, R.drawable.avatar_05),
    BEARD_GUY(9, 20, R.string.pet_donkey, R.drawable.avatar_04),
    DWIGHT(10, 20, R.string.pet_donkey, R.drawable.avatar_03),
    MICHAEL(11, 20, R.string.pet_donkey, R.drawable.avatar_02),
    TOBBY(12, 20, R.string.pet_donkey, R.drawable.avatar_01)
}