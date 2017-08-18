package io.ipoli.android

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.ipoli.android.player.Player
import io.ipoli.android.player.PlayerRepository
import io.ipoli.android.reward.*
import io.ipoli.android.util.RxSchedulersTestRule
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Rule
import org.junit.Test


/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 7/10/17.
 */
class DisplayRewardUseCaseTest {

    @Rule
    @JvmField
    val rxRule = RxSchedulersTestRule()

    @Test
    fun listIsDisplayed() {

        val playerRepoMock = mock<PlayerRepository> {
            on { get() } doReturn Observable.just(Player())
        }

        val rewardRepoMock = mock<RewardRepository> {
            on { loadRewards() } doReturn Observable.just(listOf(Reward()))
        }

        val useCase = DisplayRewardsUseCase(rewardRepoMock, playerRepoMock)
        useCase.subscribeOnScheduler = Schedulers.io()

        val observer = TestObserver<RewardStatePartialChange>()

        useCase.execute(Unit)
                .subscribe(observer)

        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValueCount(2)
        observer.assertValueAt(0, { it is RewardsLoadingPartialChange })
        observer.assertValueAt(1, { it is RewardsLoadedPartialChange })
        observer.assertValueAt(1, {
            val state = it as RewardsLoadedPartialChange
            state.data.size == 1
        })
    }
}