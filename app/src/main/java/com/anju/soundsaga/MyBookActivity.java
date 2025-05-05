package com.anju.soundsaga;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.anju.soundsaga.databinding.ActivityMybooksBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyBookActivity extends AppCompatActivity {

    private ActivityMybooksBinding binding;
    private MyBookActivityAdapter adapter;
    private List<MyBook> booksList = new ArrayList<>();
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#4B0000"));
        // Initialize ViewBinding
        binding = ActivityMybooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appName.setText("SoundSaga");
        binding.Audiobooks.setText("My Books");
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;
        // Initialize RecyclerView
        binding.recyclerview2.setLayoutManager(new GridLayoutManager(this, spanCount)); // 1 column grid layout

        // Load data from JSON file
        loadDataFromJson();

        // Set up the adapter with the data
        adapter = new MyBookActivityAdapter(booksList);
        binding.recyclerview2.setAdapter(adapter);
    }
    private void loadDataFromJson() {
        String json = loadJsonFromFile();
        if (json != null) {
            try {
                JSONArray rootArray = new JSONArray(json); // Parse JSON as an array
                List<MyBook> parsedBooks = parseAudioBooks(rootArray); // Parse all books
                booksList.addAll(parsedBooks);
                sortBooksByLastPlayedDate(booksList);  // Add all books to the list

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void sortBooksByLastPlayedDate(List<MyBook> myBooks) {
        Collections.sort(myBooks, new Comparator<MyBook>() {
            @Override
            public int compare(MyBook book1, MyBook book2) {
                try {
                    // Parse the last played date times
                    Date date1 = dateFormat.parse(book1.getLastPlayedDateTime());
                    Date date2 = dateFormat.parse(book2.getLastPlayedDateTime());

                    // Compare the dates (newer dates first)
                    return date1.compareTo(date2);  // reverse order to get most recent first
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        Collections.reverse(myBooks);
    }
    private String loadJsonFromFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("audiobook_info.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private List<MyBook> parseAudioBooks(JSONArray audioBooksArray) throws JSONException {
        List<MyBook> myBooks = new ArrayList<>();

        // Convert the JSON string into a JSONArray


        // Iterate through the array and parse each book
        for (int i = 0; i < audioBooksArray.length(); i++) {
            JSONObject audioBookJson = audioBooksArray.getJSONObject(i);
            MyBook book = parseAudioBook(audioBookJson);
            myBooks.add(book);

        }

        return myBooks;
    }

    private MyBook parseAudioBook(JSONObject audioBookJson) throws JSONException {
        String title = audioBookJson.getString("title");
        String author = audioBookJson.getString("author");
        String image = audioBookJson.getString("image");
        String currentChapter = audioBookJson.getString("currentChapter");
int position= Integer.parseInt(audioBookJson.getString("position"));
        String currentPlayTime = audioBookJson.getString("currentPlayTime");
        String totalDuration = audioBookJson.getString("totalDuration");
        String lastPlayedDateTime = audioBookJson.getString("lastPlayedDateTime");
        Log.d("datentime","dtatentime"+lastPlayedDateTime);
        String spee = audioBookJson.getString("speed");
        Log.d("bookactivityspeed","bookactivityspeed"+spee);
float speed = Float.parseFloat(audioBookJson.getString("speed"));
        // Parse chapters
        List<Chapter> chapters = new ArrayList<>();
        String chaptersString = audioBookJson.getString("chapters");
        JSONArray chaptersArray = new JSONArray(chaptersString); // Convert string to JSONArray

        for (int i = 0; i < chaptersArray.length(); i++) {
            JSONObject chapterJson = chaptersArray.getJSONObject(i);
            int number = chapterJson.getInt("number");
            String chapterTitle = chapterJson.getString("title");
            String url = chapterJson.getString("url");
            chapters.add(new Chapter(number, chapterTitle, url));
        }

        return new MyBook(title, author, image, chapters, currentChapter,position, currentPlayTime, totalDuration, lastPlayedDateTime,speed);
    }
    @Override
    protected void onResume() {
        super.onResume();
        booksList.clear();
        loadDataFromJson();

        adapter.notifyDataSetChanged();// Update UI when returning from AudioActivity
    }



}
