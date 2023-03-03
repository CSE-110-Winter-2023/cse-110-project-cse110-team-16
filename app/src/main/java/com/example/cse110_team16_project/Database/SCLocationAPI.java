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

import java.util.Map;

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

    private String url = "https://socialcompass.goto.ucsd.edu/location/";

    public static SCLocationAPI provide() {
        if (instance == null) {
            instance = new SCLocationAPI();
        }
        return instance;
    }


    public SCLocation getSCLocation(String public_code) {

        var request = new Request.Builder()
                .url(url + public_code)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            var body = response.body().string();
            Log.i("getSCLocation",body);
            return SCLocation.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putSCLocation(SCLocation scLocation, String private_code) {
        // URLs cannot contain spaces, so we replace them with %20.
        String public_code = scLocation.getPublicCode();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(scLocation,SCLocation.class);
        jsonElement.getAsJsonObject().addProperty("private_code",private_code);
        String json = gson.toJson(jsonElement);

        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url(url + public_code)
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
        String json = new Gson().toJson(Map.of("private_code", private_code));
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url(url + public_code)
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

    public void patchSCLocation(SCLocation scLocation, String private_code, boolean listed_publicly){
        String public_code = scLocation.getPublicCode();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(scLocation,SCLocation.class);
        jsonElement.getAsJsonObject().addProperty("private_code",private_code);
        jsonElement.getAsJsonObject().addProperty("is_listed_publicly",String.valueOf(listed_publicly));
        String json = gson.toJson(jsonElement);
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url(url + public_code)
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

    public void setUrl(String url) {this.url = url;}
}
