package com.anju.soundsaga;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Volley {


    public static void downloadAudioBooks(MainActivity mainActivityIn) {

        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(mainActivityIn);

        String urlToUse = "https://christopherhield.com/ABooks/abook_contents.json";



        Response.Listener<JSONArray> listener = response -> {
            try {
                Log.d("response","response"+response.toString());

                handleSuccess(response.toString(), mainActivityIn);
            } catch (JSONException e) {
                Log.d("mainactivity", "downloadAudioBooks: " + e.getMessage());
            }
        };

        Response.ErrorListener error = error1 -> handleFail(error1, mainActivityIn);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }


    private static void handleSuccess(String responseText,
                                      MainActivity mainActivity) throws JSONException {
        JSONArray response = new JSONArray(responseText);
        List<AudioBook> audioBookList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject bookObj = response.getJSONObject(i);

            String title = bookObj.getString("title");
            String author = bookObj.getString("author");
            String date = bookObj.getString("date");
            String language = bookObj.getString("language");
            String duration = bookObj.getString("duration");
            String image = bookObj.getString("image");

            // Parse Chapters
            JSONArray contentsArray = bookObj.getJSONArray("contents");
            List<Chapter> chapters = new ArrayList<>();

            for (int j = 0; j < contentsArray.length(); j++) {
                JSONObject chapterObj = contentsArray.getJSONObject(j);
                int number = chapterObj.getInt("number");
                String chapterTitle = chapterObj.getString("title");
                String url = chapterObj.getString("url");

                chapters.add(new Chapter(number, chapterTitle, url));
            }

            // Create an AudioBook object and add it to the list
            audioBookList.add(new AudioBook(title, author, date, language, duration, image, chapters));
        }

        // Pass the parsed data to the UI thread
        mainActivity.runOnUiThread(() -> mainActivity.acceptAudioBooks(audioBookList));
    }

    private static void handleFail(VolleyError ve, MainActivity mainActivity) {
        Log.d("MainActivity", "handleFail: " + ve.getMessage());
        mainActivity.runOnUiThread(() -> mainActivity.acceptFail(ve.getClass().getSimpleName()));
    }

}
