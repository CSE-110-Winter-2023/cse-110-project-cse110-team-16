package com.example.cse110_team16_project.Database;
import android.util.Log;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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

    private SCLocationAPI() {
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
        String URLCode = public_code.replace(" ", "%20");

        var request = new Request.Builder()
                .url(url + URLCode)
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
        String public_code = scLocation.getPublicCode().replace(" ", "%20");
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
            var body = response.body().string();
            Log.i("putSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSCLocation(String public_code, String private_code) {
        String URLCode = public_code.replace(" ", "%20");
        String json = new Gson().toJson(Map.of("private_code", private_code));
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url(url + URLCode)
                .method("DELETE", requestBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            var body = response.body().string();
            Log.i("deleteSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void patchSCLocation(SCLocation scLocation, String private_code, boolean listed_publicly){
        String public_code = scLocation.getPublicCode().replace(" ", "%20");;
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(scLocation,SCLocation.class);
        jsonElement.getAsJsonObject().addProperty("private_code",private_code);
        jsonElement.getAsJsonObject().addProperty("is_listed_publicly",String.valueOf(listed_publicly));
        String json = gson.toJson(jsonElement);
        RequestBody requestBody = RequestBody.create
                (json, JSON);

        var request = new Request.Builder()
                .url(url + public_code)
                .method("PATCH", requestBody)
                .build();

        try (var response = client.newCall(request).execute()) {
            var body = response.body().string();
            Log.i("patchSCLocation",body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {this.url = url;}
}
