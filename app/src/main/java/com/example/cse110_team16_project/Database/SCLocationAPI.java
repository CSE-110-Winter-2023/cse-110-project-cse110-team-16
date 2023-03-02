package com.example.cse110_team16_project.Database;
import android.util.Log;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.SCLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SCLocationAPI {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private volatile static SCLocationAPI instance = null;

    private OkHttpClient client;

    public SCLocationAPI() {
        this.client = new OkHttpClient();
    }

    public static SCLocationAPI provide() {
        if (instance == null) {
            instance = new SCLocationAPI();
        }
        return instance;
    }


    public SCLocation getSCLocation(String public_code) {

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            var body = response.body().string();
            assert body != null;
            SCLocation newSCLocation = SCLocation.fromJSON(body);
            if(newSCLocation.getPublicCode() != null) return newSCLocation;
            Log.i("getSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putSCLocation(SCLocation scLocation) {
        // URLs cannot contain spaces, so we replace them with %20.
        String public_code = scLocation.getPublicCode();
        String json = scLocation.toJSON();
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PUT", requestBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            //assert response.body() != null;
            var body = response.body().string();
            Log.i("putSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSCLocation(String public_code, String private_code) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("private_code", new JsonPrimitive(private_code));
        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PUT", requestBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            //assert response.body() != null;
            var body = response.body().string();
            Log.i("putSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void patchSCLocation(SCLocation scLocation, String private_code){
        String public_code = scLocation.getPublicCode();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(scLocation,SCLocation.class);
        jsonElement.getAsJsonObject().addProperty("private_code",private_code);
        String json = gson.toJson(jsonElement);
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PUT", requestBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            //assert response.body() != null;
            var body = response.body().string();
            Log.i("putSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
