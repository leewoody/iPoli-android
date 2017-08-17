package io.ipoli.android.player

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 8/17/17.
 */
interface SignInStatePartialChange {
    fun computeNewState(prevState: SignInViewState): SignInViewState
}

class SignInLoadingPartialChange : SignInStatePartialChange {
    override fun computeNewState(prevState: SignInViewState): SignInViewState {
        return SignInLoadingState()
    }
}

class PlayerSignedInPartialChange : SignInStatePartialChange {

    override fun computeNewState(prevState: SignInViewState): SignInViewState {
        return PlayerSignedInState()
    }
}
