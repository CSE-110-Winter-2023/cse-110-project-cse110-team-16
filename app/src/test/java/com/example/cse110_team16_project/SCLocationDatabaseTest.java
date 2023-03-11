package com.example.cse110_team16_project;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SCLocationDatabaseTest {
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SCLocationDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

    @Test
    public void testInsert(){
        SCLocation scLocation1 = new SCLocation(2,2,"Mom","1234567890");
        SCLocation scLocation2 = new SCLocation(3,3,"Mom","1234567891");
        SCLocation scLocation3 = new SCLocation(4,4,"Momn't","1234567890");

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);
        long id3 = dao.insert(scLocation3);

        assertNotEquals(id1,id3);
        assertNotEquals(id1,id2);

        SCLocation retrievedReplacedItem = dao.get("1234567890");
        assertTrue(retrievedReplacedItem.getLabel().equals("Momn't"));
        assertEquals(4, retrievedReplacedItem.getLatitude(),0.1);
        assertEquals(4, retrievedReplacedItem.getLongitude(),0.1);
    }

    @Test
    public void testExists() {
        SCLocation scLocation1 = new SCLocation(2,2,"Mom","1234567890");
        long id1 = dao.insert(scLocation1);

        assertTrue(dao.exists("1234567890"));
        assertFalse(dao.exists("1111111111"));
    }

    @Test
    public void testGet(){
        SCLocation scLocation1 = new SCLocation(2,2,"Mom","1234567890");
        long id1 = dao.insert(scLocation1);

        SCLocation retrieved = dao.get("1234567890");
        assertEquals(retrieved.getLabel(),scLocation1.getLabel());
        assertEquals(retrieved.getLatitude(),scLocation1.getLatitude(),0.001);
        assertEquals(retrieved.getLongitude(),scLocation1.getLongitude(),0.001);

    }

    //TODO
    @Test
    public void testGetLive(){
    }

    @Test
    public void testGetAll(){
        SCLocation scLocation1 = new SCLocation(2,2,"Mom","1234567890");
        SCLocation scLocation2 = new SCLocation(3,3,"Mom","1234567891");

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);

        List<SCLocation> retrievedLocations = dao.getAll();

        assertEquals(2, retrievedLocations.get(0).getLatitude(), 0.001);
        assertEquals(2, retrievedLocations.get(0).getLongitude(), 0.001);
        assertEquals(3, retrievedLocations.get(1).getLatitude(), 0.001);
        assertEquals(3, retrievedLocations.get(1).getLongitude(), 0.001);
    }

    @Test
    public void testGetAllPublicCodes(){
        SCLocation scLocation1 = new SCLocation(2,2,"Mom","1234567890");
        SCLocation scLocation2 = new SCLocation(3,3,"Mom","1234567891");

        long id1 = dao.insert(scLocation2);
        long id2 = dao.insert(scLocation1);


        List<String> retrievedCodes = dao.getAllPublicCodes();
        assertEquals(retrievedCodes.get(0),"1234567890");
        assertEquals(retrievedCodes.get(1),"1234567891");
    }

    @Test
    public void testDeleteByCode() {
        SCLocation scLocation1 = new SCLocation(2, 2, "Mom", "1234567890");
        SCLocation scLocation2 = new SCLocation(3, 3, "Mom", "1234567891");

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);


        dao.deleteByCode("1234567890");

        SCLocation removedItem = dao.get("1234567890");
        assertNull(removedItem);
        assertTrue(dao.exists("1234567891"));
    }

    @Test
    public void testDelete() {
        SCLocation scLocation1 = new SCLocation(2, 2, "Mom", "1234567890");
        SCLocation scLocation2 = new SCLocation(3, 3, "Mom", "1234567891");

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);


        int del = dao.delete(scLocation1);

        SCLocation removedItem = dao.get("1234567890");
        assertNull(removedItem);
        assertTrue(dao.exists("1234567891"));
    }

}
