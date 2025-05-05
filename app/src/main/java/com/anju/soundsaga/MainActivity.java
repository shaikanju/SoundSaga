package com.anju.soundsaga;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.anju.soundsaga.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<AudioBook> audiobooks = new ArrayList<>();
    private boolean keepOn = true;
    private static final long minSplashTime = 2000;
    private long startTime;
    private AudioBookAdapter audioBookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#4B0000"));
        startTime = System.currentTimeMillis();
        if (!isInternetAvailable()) {
            showNoInternetDialog();
            return; // Stop execution if no internet
        }
        Volley.downloadAudioBooks(this);
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(
                        new SplashScreen.KeepOnScreenCondition() {
                            @Override
                            public boolean shouldKeepOnScreen() {
                                Log.d("Main Activity", "shouldKeepOnScreen: " + (System.currentTimeMillis() - startTime));
                                return  (keepOn || System.currentTimeMillis() - startTime <= minSplashTime);
                            }
                        }
                );
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isJsonFileEmpty()) {
                    // Show an alert dialog when the JSON file is empty
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("My Books shelf is empty")
                            .setMessage("You currently have no audio books in progress.")
                            .setIcon(R.drawable.logo) // Assuming the logo is stored in drawable as logo.png
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog when OK is clicked
                                }
                            })
                            .show();
                } else {
                    // If the JSON file is not empty, proceed to open MyBookActivity
                    Intent intent = new Intent(MainActivity.this, MyBookActivity.class);
                    startActivity(intent);
                }
            }
                // Create an Intent to open MyBookActivity
//                Intent intent = new Intent(MainActivity.this, MyBookActivity.class);
//
//                // Optional: If you need to pass any data, you can add extras to the Intent
//                // intent.putExtra("key", value);
//
//                // Start MyBookActivity
//                startActivity(intent);
//            }
        });
        binding.imageView.setImageResource(R.drawable.my_books_menu);

         audioBookAdapter =
                new AudioBookAdapter(this, audiobooks);
        binding.recycler.setAdapter(audioBookAdapter);
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;

        binding.recycler.setLayoutManager(
                new GridLayoutManager(this, spanCount));



    }
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void showNoInternetDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and restart the app.")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Close the app
                    }
                })
                .show();
    }
    private boolean isJsonFileEmpty() {
        // Get the file path
        File file = new File(getFilesDir(), "audiobook_info.json");

        // Check if the file exists and is not empty
        if (file.exists() && file.length() > 0) {
            try {
                // Read the file content
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr);

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // Convert string content to JSON array
                String jsonContent = stringBuilder.toString();
                JSONArray jsonArray = new JSONArray(jsonContent);

                // Return true if the JSON array is empty
                return jsonArray.length() == 0;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        // If file doesn't exist or is empty, return true
        return true;
    }

    public void acceptAudioBooks(List<AudioBook> audioBooks) {
        keepOn = false;
        // Handle the received audiobooks list (e.g., display in RecyclerView)
      audiobooks.addAll(audioBooks);
        audioBookAdapter.notifyDataSetChanged();
    }
     // Simulated error condition

    public void acceptFail(String msg) {

        keepOn = false;
        Log.d("MainActivity", "Request failed: " + msg);
        Toast.makeText(this, "Error: " + msg + ". The app will now exit.", Toast.LENGTH_LONG).show();

        // Remove the splash screen (if applicable)
        // You can hide the splash screen, finish the activity, or transition as needed
        finish();

        // Exit the app completely
        System.exit(0);
        // Remove the splash screen
    }
}