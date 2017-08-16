package io.ipoli.android.reward

import io.reactivex.Observable


/**
 * Created by Venelin Valkov <venelin@curiousily.com> on 7/9/17.
 */
class RewardListInteractor {

    fun useReward(reward: Reward): Observable<Reward> {
        return Observable.just(reward)
    }

    fun loadRewards(): Observable<List<Reward>> {
        val rewardRepository = RewardRepository()
        return rewardRepository.loadRewards()
    }

    fun deleteReward(reward: Reward): Observable<Unit> {
        val rewardRepository = RewardRepository()
        rewardRepository.delete(reward)
        return Observable.just(Unit)
    }
}