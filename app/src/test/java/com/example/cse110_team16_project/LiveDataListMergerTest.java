package com.example.cse110_team16_project;

import static com.example.cse110_team16_project.classes.LiveDataListMerger.UPDATE_TIME;
import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.LiveDataListMerger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class LiveDataListMergerTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testUpdating(){
        List<LiveData<Double>> listToConvert = new ArrayList<>();
        MutableLiveData<Double> l1 = new MutableLiveData<>();
        MutableLiveData<Double> l2 = new MutableLiveData<>();
        MutableLiveData<Double> l3 = new MutableLiveData<>();

        listToConvert.add(l1);
        listToConvert.add(l2);
        listToConvert.add(l3);

        LiveDataListMerger<Double> merger = new LiveDataListMerger<>();
        merger.startObserving(listToConvert);
        try {
            Thread.sleep(UPDATE_TIME + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(3, merger.getMergedList().getValue().size());
        l1.postValue(3.0);
        l2.postValue(4.0);
        l3.postValue(5.0);
        try {
            Thread.sleep(UPDATE_TIME + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals((Double)3.0, merger.getMergedList().getValue().get(0));
        assertEquals((Double)4.0, merger.getMergedList().getValue().get(1));
        assertEquals((Double)5.0, merger.getMergedList().getValue().get(2));
        merger.stopUpdating();
        l1.postValue(1.0);
        l2.postValue(1.0);
        l3.postValue(1.0);
        try {
            Thread.sleep(UPDATE_TIME + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals((Double)3.0, merger.getMergedList().getValue().get(0));
        assertEquals((Double)4.0, merger.getMergedList().getValue().get(1));
        assertEquals((Double)5.0, merger.getMergedList().getValue().get(2));
    }
}
