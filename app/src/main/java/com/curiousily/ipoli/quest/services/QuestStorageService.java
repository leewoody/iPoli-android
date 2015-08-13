package com.curiousily.ipoli.quest.services;

import com.curiousily.ipoli.app.APIClient;
import com.curiousily.ipoli.quest.Quest;
import com.curiousily.ipoli.quest.events.CreateQuestEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 8/13/15.
 */
public class QuestStorageService {
    private final APIClient apiClient;
    private final Bus bus;

    public QuestStorageService(APIClient api, Bus bus) {
        this.apiClient = api;
        this.bus = bus;
    }

    @Subscribe
    public void onCreateQuest(CreateQuestEvent e) {
        apiClient.createQuest(e.quest, new Callback<Quest>() {
            @Override
            public void success(Quest quest, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
