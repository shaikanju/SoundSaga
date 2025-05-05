package com.anju.soundsaga;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.anju.soundsaga.databinding.ActivityAudiobookBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioBookActivity extends AppCompatActivity {
    private ActivityAudiobookBinding binding;
    private BookActivityAdapter adapter;
    private List<Chapter> chapterList = new ArrayList<>();
    private MediaPlayer player;
    private int startTime;
    private int currentPosition = 0;
    private PopupMenu popupMenu;
    private float speed;
    private AudioBook audioBook;
    private MyBook mybook;
    private String audioUrl;
    private Timer timer;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#4B0000"));
        binding = ActivityAudiobookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.booktitle.setSelected(true);
        binding.apptitle.setText("SoundSaga");

        // Extract intent data
        Intent intent = getIntent();

// Check if the intent is not null and if it contains a source identifier
            if (intent != null) {
                // Check for the source of the Intent (MainActivity or MyBooksActivity)
                String sourceActivity = intent.getStringExtra("source");

                if ("MainActivity".equals(sourceActivity)) {
                    // Actions when the source is MainActivity
                    if (intent.hasExtra("audioBook")) {
                        audioBook = (AudioBook) intent.getSerializableExtra("audioBook");

                        if (audioBook != null) {
                            binding.booktitle.setText(audioBook.getTitle());
                            binding.booktitle.setSelected(true);
                            binding.backward15.setImageResource(R.drawable.back);
                            binding.forward15.setImageResource(R.drawable.fore);
                            binding.playnpause.setImageResource(R.drawable.pause);
                        }
                    }

                    // Load chapter list (Replace with real data fetching logic)
                    chapterList = audioBook.getContents(); // Replace with actual chapter list fetching logic
                    adapter = new BookActivityAdapter(audioBook.getImage(), chapterList, this);
                    binding.viewPager2.setAdapter(adapter);
                    binding.chaptername.setText(chapterList.get(currentPosition).getTitle());
                    binding.chaptername.setSelected(true);
                    // Set up ViewPager2
                    binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    updateArrowVisibility();
                    player = new MediaPlayer();
                    setupSeekBar();
                    setupSpeedMenu();

                    presetVars();
                    binding.speed.setText("1.0x");
                    binding.speed.setPaintFlags(binding.speed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    // Handle saved instance state
                    if (savedInstanceState != null) {
                        updateFromBundle(savedInstanceState);
                    }

                    // Play the first chapter's audio
                    if (!chapterList.isEmpty()) {
                        playChapterAudio(0);
                    }
                }
                else if ("MyBookActivity".equals(sourceActivity))
                {
                    if (intent.hasExtra("audioBook")) {

                        mybook = (MyBook) intent.getSerializableExtra("audioBook");


                        if (mybook != null) {
                            binding.booktitle.setText(mybook.getTitle());
                            binding.booktitle.setSelected(true);
                            binding.backward15.setImageResource(R.drawable.back);
                            binding.forward15.setImageResource(R.drawable.fore);
                            binding.playnpause.setImageResource(R.drawable.pause);
                        }

                    }
                    currentPosition = mybook.getPosition();
                    Log.d("currentpos","cp"+currentPosition);
                    int timeElapsed = Integer.parseInt(mybook.getCurrentPlayTime());
                    Log.d("timeElapsed","timeelapsed"+timeElapsed);
                    speed = mybook.getSpeed();
                    chapterList = mybook.getChapters();
                    Log.d("chapterlist","chapterlist"+chapterList);
                    adapter = new BookActivityAdapter(mybook.getImage(), chapterList, this);
                    binding.viewPager2.setAdapter(adapter);
                    binding.viewPager2.setCurrentItem(currentPosition, false);
                    binding.chaptername.setText(chapterList.get(currentPosition).getTitle());
                    binding.chaptername.setSelected(true);
                    binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    updateArrowVisibility();
                    player = new MediaPlayer();
                    setupSeekBar();
                    setupSpeedMenu();

                    // Preset other variables (e.g., play speed)
//                    presetVars();
                    if(speed>1.0)
                    {
                        binding.speed.setText(String.format("%.2fx",speed));
                    }
                    else
                    {
                        binding.speed.setText(String.format("%.1fx",speed));
                    }

                    binding.speed.setPaintFlags(binding.speed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    // Handle saved instance state
                    if (savedInstanceState != null) {
                        updateFromBundle(savedInstanceState);
                    }

                    // Play the first chapter's audio or resume from the given position
                    if (!chapterList.isEmpty()) {
                        playChapterAudio(currentPosition, timeElapsed);  // Play audio from the specified chapter and time
                    }

                }
            }


        player.setOnCompletionListener(mediaPlayer -> {
            timer.cancel();
            updateArrowVisibility();

            if (currentPosition < chapterList.size() - 1) { // Ensure it doesn't exceed the list
                currentPosition++;
                audioUrl = chapterList.get(currentPosition).getUrl();

                if (player != null) {
                    player.stop();
                    player.reset();
                }


                // Update ViewPager2 to the next chapter
                runOnUiThread(() -> binding.viewPager2.setCurrentItem(currentPosition, true));
                playChapterAudio(currentPosition);
                binding.chaptername.setText(chapterList.get(currentPosition).getTitle());


                // Start playing the next chapter's audio
//                playChapterAudio(0);

            }

        });
        binding.leftArrow.setOnClickListener(v -> {
            int currentPos = binding.viewPager2.getCurrentItem();

            if (currentPos > 0) {
                binding.viewPager2.setCurrentItem(currentPos - 1, true);
            }
            updateArrowVisibility();
        });

        binding.rightArrow.setOnClickListener(v -> {
            int currentPos = binding.viewPager2.getCurrentItem();

            if (currentPos < chapterList.size() - 1) {
                binding.viewPager2.setCurrentItem(currentPos + 1, true);
            }
            updateArrowVisibility();
        });

//         ViewPager2 listener to change audio based on page
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {

                updateArrowVisibility();
                super.onPageSelected(position);
//                currentPosition = position;
//                Log.d("page changed","page changed");
//                playChapterAudio(currentPosition,0);
//                binding.chaptername.setText(chapterList.get(currentPosition).getTitle());
                if (currentPosition != position) {  // Only play if it's a new chapter
                    currentPosition = position;
                    Log.d("Page Changed", "New chapter: " + currentPosition);
                    playChapterAudio(currentPosition);
                    binding.chaptername.setText(chapterList.get(currentPosition).getTitle());
                }
            }


        });


        // Play/Pause button functionality
        binding.playnpause.setOnClickListener(v -> togglePlayback());

        // Seek backward
        binding.backward15.setOnClickListener(v -> seekBackward());

        // Seek forward
        binding.forward15.setOnClickListener(v -> seekForward());
    }

private void playChapterAudio(int position) {
    try {
        // Stop and reset the media player before loading new audio
        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();

        // Set the new audio source
        audioUrl = chapterList.get(position).getUrl();
        player.setDataSource(audioUrl);


        // Prepare asynchronously to avoid UI blocking
        player.prepareAsync();

        // Set listener to execute actions after preparation is complete
        player.setOnPreparedListener(mp -> {
            Log.d("MediaPlayer", "Before seekTo(0): " + mp.getCurrentPosition());

            // Ensure the seek bar max value is set AFTER media is prepared
            binding.seekBar.setMax(mp.getDuration());

            // Apply playback speed
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));

            // Force start from the beginning
            mp.seekTo(0);
            Log.d("MediaPlayer", "After seekTo(0): " + mp.getCurrentPosition());

            // Start playback
            mp.start();
            timerCounter();
        });

    } catch (Exception e) {

        Log.e("MainActivity", "Error in playChapterAudio: " + e.getMessage());

        // Show a toast with the error message

    }
}

    private void playChapterAudio(int position, int timeElapsed) {
        if (position < 0 || position >= chapterList.size()) {
            return; // Invalid position, do nothing
        }

        // Get the chapter URL (replace with actual logic to fetch the URL)
        String audioUrl = chapterList.get(position).getUrl();


        try {
            if (player.isPlaying()) {
                Log.d("hello","hello i am playing");
                player.stop();  // Stop the current audio if it's playing
            }

            player.reset();  // Reset the player for the new audio

            // Prepare the player with the new audio URL
            player.setDataSource(audioUrl);
            player.prepareAsync();  // Asynchronous preparation

            player.setOnPreparedListener(mp -> {

                int dur = player.getDuration();
                binding.seekBar.setMax(dur);
                // Seek to the timeElapsed position in the chapter
                if (timeElapsed > 0) {

                    player.seekTo(timeElapsed);
                    player.setOnSeekCompleteListener(seekMp -> {
                        player.start(); // Start only after seeking completes
                    });// Seek to the elapsed time if available
                }
                else {
                    Log.d("hey there","hey"+timeElapsed);
                    player.start(); // Start immediately if no seek is needed
                }

                // Start the audio

                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));

                // Start or restart the timer counter
                if (timer != null) {
                    timer.cancel(); // Cancel previous timer if it exists
                }
                timerCounter();

                // Update the play/pause button UI
//              binding.playnpause.setImageResource(R.drawable.pause);
            });
        } catch (IOException e) {
            e.printStackTrace();

        }}
    @Override
    public void onBackPressed() {
        super.onBackPressed();

//      clearAudioBookJson();
        // Save the current audiobook information when back button is pressed
        saveAudioBookToJson();
    }
    private void clearAudioBookJson() {
        File file = new File(getFilesDir(), "audiobook_info.json");
        if (file.exists()) {
            if (file.delete()) {
                Log.d("AudioBookJson", "Audiobook JSON file deleted successfully.");
            } else {
                Log.e("AudioBookJson", "Failed to delete audiobook JSON file.");
            }
        }
    }
private void saveAudioBookToJson() {
    if (audioBook != null) {
        File file = new File(getFilesDir(), "audiobook_info.json");
        JSONArray jsonArray = new JSONArray(); // To store the audiobook list

        try {
            // Read existing data if the file exists
            if (file.exists()) {
                String existingJson = readJsonFromFile(file);

                if (!existingJson.isEmpty()) {
                    jsonArray = new JSONArray(existingJson);
                }
            }

            // Convert current audiobook to JSON
            JSONObject newBookJson = createJsonFromAudioBook(audioBook);

            // Check if the book with the same title already exists in the array
            boolean bookExists = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingBook = jsonArray.getJSONObject(i);
                if (existingBook.getString("title").equals(audioBook.getTitle())) {
                    // Update the existing book entry
                    jsonArray.put(i, newBookJson);
                    bookExists = true;
                    break;
                }
            }

            // If it's a new book, add it to the array
            if (!bookExists) {
                jsonArray.put(newBookJson);
            }

            // Write the updated JSON array back to the file
            FileWriter fileWriter = new FileWriter(file, false); // Overwrite the file with updated data
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonArray.toString()); // Write the updated JSON array
            writer.close();

            Log.d("AudioBookJson", "Updated JSON saved to file: " + jsonArray.toString());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    else if(mybook!=null)
    {
        Log.d("mybookgetting saved","hello this is the my book getting saved");
        File file = new File(getFilesDir(), "audiobook_info.json");
        JSONArray jsonArray = new JSONArray(); // To store the audiobook list

        try {
            // Read existing data if the file exists
            if (file.exists()) {
                String existingJson = readJsonFromFile(file);

                if (!existingJson.isEmpty()) {
                    jsonArray = new JSONArray(existingJson);
                }
            }

            // Convert current audiobook to JSON
            JSONObject newBookJson = createJsonFromAudioBook(mybook);

            // Check if the book with the same title already exists in the array
            boolean bookExists = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingBook = jsonArray.getJSONObject(i);
                if (existingBook.getString("title").equals(mybook.getTitle())) {
                    // Update the existing book entry
                    jsonArray.put(i, newBookJson);
                    bookExists = true;
                    break;
                }
            }

            // If it's a new book, add it to the array
            if (!bookExists) {
                jsonArray.put(newBookJson);
            }

            // Write the updated JSON array back to the file
            FileWriter fileWriter = new FileWriter(file, false); // Overwrite the file with updated data
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonArray.toString()); // Write the updated JSON array
            writer.close();

            Log.d("AudioBookJson", "Updated JSON saved to file: " + jsonArray.toString());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

}

    private String readJsonFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            // Create a FileReader and BufferedReader to read the content of the file
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;

            // Read each line from the file and append it to the StringBuilder
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close(); // Close the reader

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString(); // Return the entire file content as a string
    }
    private JSONObject  createJsonFromAudioBook(MyBook audioBook) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm"); // HH for 24-hour format
        String currentDateTime = sdf.format(new Date()); // Get the current date and time
        JSONObject json = new JSONObject();

        // Manually create the JSON string from AudioBook object's fields


        try {
            json.put("title", audioBook.getTitle());
            json.put("author", audioBook.getAuthor());
            json.put("image", audioBook.getImage());
            json.put("chapters", chaptersToJson(audioBook.getChapters()));
            json.put("currentChapter", chapterList.get(currentPosition).getTitle());
            json.put("position", currentPosition);

            Log.d("hello", "hello"+currentPosition);
            json.put("speed",speed);
            Log.d("speed", "speed "+speed);
            json.put("currentPlayTime", player.getCurrentPosition());

            json.put("totalDuration", player.getDuration());
            json.put("lastPlayedDateTime", currentDateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    private JSONObject  createJsonFromAudioBook(AudioBook audioBook) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm"); // HH for 24-hour format
        String currentDateTime = sdf.format(new Date()); // Get the current date and time
        JSONObject json = new JSONObject();

        // Manually create the JSON string from AudioBook object's fields


        try {
            json.put("title", audioBook.getTitle());
            json.put("author", audioBook.getAuthor());
            json.put("image", audioBook.getImage());
            json.put("chapters", chaptersToJson(audioBook.getContents()));
            json.put("currentChapter", chapterList.get(currentPosition).getTitle());
            json.put("position", currentPosition);

            Log.d("hello", "hello"+currentPosition);
            json.put("speed",speed);
            Log.d("speed", "speed "+speed);
            json.put("currentPlayTime", player.getCurrentPosition());
            json.put("totalDuration", player.getDuration());
            json.put("lastPlayedDateTime", currentDateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    private String chaptersToJson(List<Chapter> chapters) {
        StringBuilder chaptersJson = new StringBuilder("[");
        for (Chapter chapter : chapters) {
            chaptersJson.append("{");
            chaptersJson.append("\"number\": ").append(chapter.getNumber()).append(", ");
            chaptersJson.append("\"title\": \"").append(chapter.getTitle()).append("\", ");
            chaptersJson.append("\"url\": \"").append(chapter.getUrl()).append("\"");
            chaptersJson.append("},");
        }
        if (chaptersJson.length() > 1) {
            chaptersJson.setLength(chaptersJson.length() - 1); // Remove last comma
        }
        chaptersJson.append("]");
        return chaptersJson.toString();
    }

    private void updateArrowVisibility() {
        int currentPos = binding.viewPager2.getCurrentItem();

        // Hide left arrow at the first chapter, show otherwise
        if (currentPos == 0) {
            binding.leftArrow.setVisibility(View.INVISIBLE);
        } else {
            binding.leftArrow.setVisibility(View.VISIBLE);
        }

        // Hide right arrow at the last chapter, show otherwise
        if (currentPos == chapterList.size() - 1) {
            binding.rightArrow.setVisibility(View.INVISIBLE);
        } else {
            binding.rightArrow.setVisibility(View.VISIBLE);
        }
    }
    private void setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        // Don't need
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Don't need
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progress = seekBar.getProgress();
                        player.seekTo(progress);

                    }
                });
    }
    private void presetVars() {

        startTime = 0;
        speed = 1.0f;
    }
    public boolean isUrlAccessible(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");  // Only check for availability, no full download
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;  // 200 OK
        } catch (IOException e) {
            return false;
        }
    }

    private void setupSpeedMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        popupMenu = new PopupMenu(wrapper, binding.speed);

        popupMenu.getMenuInflater().inflate(R.menu.speed_popup, popupMenu.getMenu());
        View menuView = popupMenu.getMenu().getItem(0).getActionView();
        if (menuView != null) {
            menuView.setBackgroundColor(Color.parseColor("#800000"));
        } else {
            Log.e("AudioBookActivity", "Menu View is null");}
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_075) {
                speed = 0.75f;
            } else if (menuItem.getItemId() == R.id.menu_1) {
                speed = 1f;
            } else if (menuItem.getItemId() == R.id.menu_11) {
                speed = 1.1f;
            } else if (menuItem.getItemId() == R.id.menu_125) {
                speed = 1.25f;
            } else if (menuItem.getItemId() == R.id.menu_15) {
                speed = 1.5f;
            } else if (menuItem.getItemId() == R.id.menu_175) {
                speed = 1.75f;
            } else if (menuItem.getItemId() == R.id.menu_2) {
                speed = 2f;
            }

            binding.speed.setText(menuItem.getTitle());
            binding.speed.setPaintFlags(binding.speed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
            return true;
        });
    }


    private void timerCounter() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (player != null && player.isPlaying()) {
                        binding.seekBar.setProgress(player.getCurrentPosition());

                        binding.timeelapsed.setText(
                                String.format("%s",
                                        getTimeStamp(player.getCurrentPosition())));
                        binding.chapterduration.setText(
                                String.format("%s",
                                        getTimeStamp(player.getDuration())));

                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
    private String getTimeStamp(int ms) {
        int t = ms;
        int h = ms / 3600000;
        t -= (h * 3600000);
        int m = t / 60000;
        t -= (m * 60000);
        int s = t / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }
    private void togglePlayback() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                binding.playnpause.setImageResource(R.drawable.play);
            } else {
                player.start();
                binding.playnpause.setImageResource(R.drawable.pause);
            }
        }
    }
    private void seekBackward() {
        if (player != null) {
            int currentPosition = player.getCurrentPosition();
            player.seekTo(Math.max(currentPosition - 15000, 0)); // Go back 15 seconds
        }
    }
    private void seekForward() {
        if (player != null) {
            int currentPosition = player.getCurrentPosition();
            int duration = player.getDuration();
            player.seekTo(Math.min(currentPosition + 15000, duration)); // Skip forward 15 seconds
        }
    }
    protected void onDestroy() {
        timer.cancel();
        player.release();
        player = null;
        super.onDestroy();
    }
    public void speedClick(View v) {


        popupMenu.show();
    }
    private void updateFromBundle(Bundle bundle) {

        audioUrl = bundle.getString("URL");
        startTime = bundle.getInt("TIME");
        speed = bundle.getFloat("SPEED");
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("URL", audioUrl);
        outState.putInt("TIME", player.getCurrentPosition());
        outState.putFloat("SPEED", speed);
        super.onSaveInstanceState(outState);
    }
}
