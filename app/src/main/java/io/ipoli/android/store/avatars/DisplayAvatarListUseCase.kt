package io.ipoli.android.store.avatars

import io.ipoli.android.common.BaseRxUseCase
import io.ipoli.android.common.SimpleRxUseCase
import io.ipoli.android.player.persistence.PlayerRepository
import io.ipoli.android.player.persistence.RealmPlayerRepository
import io.ipoli.android.store.StoreLoadedPartialChange
import io.ipoli.android.store.StoreLoadingPartialChange
import io.ipoli.android.store.StoreStatePartialChange
import io.ipoli.android.store.avatars.data.Avatar
import io.reactivex.Observable

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 8/21/17.
 */
class DisplayAvatarListUseCase(private val playerRepository: PlayerRepository) : SimpleRxUseCase<AvatarListPartialStateChange>() {

    override fun createObservable(params: Unit): Observable<AvatarListPartialStateChange> =
        playerRepository.findFirst()
            .map { player ->
                AvatarListLoadedPartialStateChange(Avatar.values().map { AvatarViewModel(it.code, it.avatarName,
                    it.price, it.picture, false) }) as AvatarListPartialStateChange
            }
            .startWith(AvatarListLoadingPartialStateChange())
}