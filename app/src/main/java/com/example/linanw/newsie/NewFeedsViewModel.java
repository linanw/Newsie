package com.example.linanw.newsie;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class NewFeedsViewModel extends ViewModel {
    // Create a LiveData with a String
    private MutableLiveData<String> defaultFeedJson;

    public MutableLiveData<String> getCurrentName() {
        if (defaultFeedJson == null) {
            defaultFeedJson = new MutableLiveData<String>();
        }
        return defaultFeedJson;
    }
}
