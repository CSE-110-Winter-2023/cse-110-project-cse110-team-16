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
        SCLocationDatabase.inject(db);
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

    @Test
    public void testInsert(){
        String public_code1 = "SCLocationDatabaseTest1Public1";
        String public_code2 = "SCLocationDatabaseTest1Public2";
        String label1 = "testLabel1";
        String label2 = "testLabel2";
        SCLocation scLocation1 = new SCLocation(2,2,label1,public_code1);
        SCLocation scLocation2 = new SCLocation(3,3,label1,public_code2);
        SCLocation scLocation3 = new SCLocation(4,4,label2,public_code1);

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);
        long id3 = dao.insert(scLocation3);

        assertNotEquals(id1,id3);
        assertNotEquals(id1,id2);

        SCLocation retrievedReplacedItem = dao.get(public_code1);
        assertEquals(label2, retrievedReplacedItem.getLabel());
        assertEquals(4, retrievedReplacedItem.getLatitude(),0.1);
        assertEquals(4, retrievedReplacedItem.getLongitude(),0.1);
    }

    @Test
    public void testExists() {
        String public_code1 = "SCLocationDatabaseTest2Public1";
        String public_code2 = "SCLocationDatabaseTest2Public2"; //should be different from public_code1
        String label = "testLabel";

        SCLocation scLocation1 = new SCLocation(2,2,label,public_code1);
        long id1 = dao.insert(scLocation1);

        assertTrue(dao.exists(public_code1));
        assertFalse(dao.exists(public_code2));
    }

    @Test
    public void testGet(){
        String public_code = "SCLocationDatabaseTest3Public";
        String label = "testLabel";

        SCLocation scLocation1 = new SCLocation(2,2,label,public_code);
        long id1 = dao.insert(scLocation1);

        SCLocation retrieved = dao.get(public_code);
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
        String public_code1 = "SCLocationDatabaseTest4Public1";
        String public_code2 = "SCLocationDatabaseTest4Public2";
        String label1 = "testLabel1";
        String label2 = "testLabel2";
        SCLocation scLocation1 = new SCLocation(2,2,label1,public_code1);
        SCLocation scLocation2 = new SCLocation(3,3,label2,public_code2);

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

        String public_code1 = "SCLocationDatabaseTest5Public1"; //public_code1 should come alphabetically before public_code2
        String public_code2 = "SCLocationDatabaseTest5Public2";
        String label1 = "testLabel1";
        String label2 = "testLabel2";
        SCLocation scLocation1 = new SCLocation(2,2,label1,public_code1);
        SCLocation scLocation2 = new SCLocation(3,3,label2,public_code2);

        long id1 = dao.insert(scLocation2);
        long id2 = dao.insert(scLocation1);


        List<String> retrievedCodes = dao.getAllPublicCodes();
        assertEquals(retrievedCodes.get(0),public_code1);
        assertEquals(retrievedCodes.get(1),public_code2);
    }

    @Test
    public void testDeleteByCode() {
        String public_code1 = "SCLocationDatabaseTest6Public1";
        String public_code2 = "SCLocationDatabaseTest6Public2";
        String label1 = "testLabel1";
        String label2 = "testLabel2";
        SCLocation scLocation1 = new SCLocation(2,2,label1,public_code1);
        SCLocation scLocation2 = new SCLocation(3,3,label2,public_code2);

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);


        dao.deleteByCode(public_code1);

        SCLocation removedItem = dao.get(public_code1);
        assertNull(removedItem);
        assertTrue(dao.exists(public_code2));
    }

    @Test
    public void testDelete() {
        String public_code1 = "SCLocationDatabaseTest7Public1";
        String public_code2 = "SCLocationDatabaseTest7Public2";
        String label1 = "testLabel1";
        String label2 = "testLabel2";
        SCLocation scLocation1 = new SCLocation(2,2,label1,public_code1);
        SCLocation scLocation2 = new SCLocation(3,3,label2,public_code2);

        long id1 = dao.insert(scLocation1);
        long id2 = dao.insert(scLocation2);


        int del = dao.delete(scLocation1);

        SCLocation removedItem = dao.get(public_code1);
        assertNull(removedItem);
        assertTrue(dao.exists(public_code2));
    }

}
