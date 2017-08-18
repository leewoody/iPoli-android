package io.ipoli.android.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.RestoreViewOnCreateMviController
import io.ipoli.android.R
import io.ipoli.android.daggerComponent
import io.ipoli.android.navigator
import io.ipoli.android.player.DaggerSignInComponent
import kotlinx.android.synthetic.main.controller_store.view.*

/**
 * Created by Polina Zhelyazkova <polina@ipoli.io>
 * on 8/18/17.
 */
class StoreController : RestoreViewOnCreateMviController<StoreController, StorePresenter>() {

    val storeComponent : StoreComponent by lazy {
        val component = DaggerStoreComponent
                .builder()
                .controllerComponent(daggerComponent)
                .build()
        component.inject(this@StoreController)
        component
    }

    override fun createPresenter(): StorePresenter = storeComponent.createStorePresenter()

    override fun setRestoringViewState(restoringViewState: Boolean) {}

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        storeComponent // will ensure that dagger component will be initialized lazily.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.controller_store, container, false)
        view.subscriptionContainer.setOnClickListener({ navigator.showRewardsList() })
        view.powerUpsContainer.setOnClickListener({ navigator.showRewardsList() })
        view.avatarsContainer.setOnClickListener({ navigator.showRewardsList() })
        view.petsContainer.setOnClickListener({ navigator.showRewardsList() })
        return view
    }

}

