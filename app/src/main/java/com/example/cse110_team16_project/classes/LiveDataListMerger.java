package com.example.cse110_team16_project.classes;

import static com.example.cse110_team16_project.Database.SCLocationRepository.LIVE_UPDATE_TIME_MS;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public class LiveDataListMerger<T> {
    public static final int UPDATE_TIME = LIVE_UPDATE_TIME_MS;
    private ScheduledFuture<?> future;
    private MediatorLiveData<List<T>> mergedLiveData = new MediatorLiveData<>();

    public LiveDataListMerger(){
    }

    public void stopUpdating() {
        if (this.future != null && !this.future.isCancelled()) {
            future.cancel(true);
        }
    }

    public void startObserving(@Nonnull List<LiveData<T>> liveDataList){
        var executor = Executors.newSingleThreadScheduledExecutor();
        future = executor.scheduleAtFixedRate(() ->
        {
            List<T> dataList = new ArrayList<>(liveDataList.size());
            for(int i = 0; i < liveDataList.size(); i++){
                dataList.add(liveDataList.get(i).getValue());
            }
            mergedLiveData.postValue(dataList);
        },0,UPDATE_TIME, TimeUnit.MILLISECONDS);
    }

    public LiveData<List<T>> getMergedList() {
        return this.mergedLiveData;
    }
}
