package com.example.cse110_team16_project.classes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LiveDataListMerger<T> {

    private ScheduledFuture<?> future;
    private MediatorLiveData<List<T>> mergedLiveData = new MediatorLiveData<>();

    public LiveDataListMerger(List<LiveData<T>> liveDataList){
        var executor = Executors.newSingleThreadScheduledExecutor();
        future = executor.scheduleAtFixedRate(() ->
        {
            List<T> dataList = new ArrayList<>(liveDataList.size());
            for(int i = 0; i < liveDataList.size(); i++){
                dataList.add(liveDataList.get(i).getValue());
            }
            mergedLiveData.postValue(dataList);
        },0,3000, TimeUnit.MILLISECONDS);
    }

    public void stopUpdating() {
        if (this.future != null && !this.future.isCancelled()) {
            future.cancel(true);
        }
    }
}
