package com.anju.soundsaga;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class MyBookActivityAdapter extends RecyclerView.Adapter<MyBookActivityViewHolder> {

        private List<MyBook> myBooks;

        public MyBookActivityAdapter(List<MyBook> myBooks) {
            this.myBooks = myBooks;
        }

        @NonNull
        @Override
        public MyBookActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout for each item
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_book, parent, false);
            return new MyBookActivityViewHolder(itemView);
        }
    private String convertMillisecondsToMMSS(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds); // Format as mm:ss
    }

        @Override
        public void onBindViewHolder(@NonNull MyBookActivityViewHolder holder, int position) {

            // Get the book item from the list
            MyBook currentBook = myBooks.get(position);

            // Set data for the views
            holder.bookTitle.setText(currentBook.getTitle());
            holder.authorName.setText(currentBook.getAuthor());
            holder.chapterName.setText(currentBook.getCurrentChapter());
            holder.chapterName.setSelected(true);
            long currentPlayTimeMillis = Long.parseLong(currentBook.getCurrentPlayTime());
            long totalDurationMillis = Long.parseLong(currentBook.getTotalDuration());

// Convert both times to mm:ss format
            String currentPlayTimeFormatted = convertMillisecondsToMMSS(currentPlayTimeMillis);
            String totalDurationFormatted = convertMillisecondsToMMSS(totalDurationMillis);
            holder.itemView.setOnClickListener(v ->{
                Intent intent = new Intent(v.getContext(), AudioBookActivity.class);
                intent.putExtra("audioBook", currentBook);
                intent.putExtra("source", "MyBookActivity");// Pass the AudioBook object
                v.getContext().startActivity(intent);
            });
// Set the formatted text
            holder.timeGone.setText(currentPlayTimeFormatted + " of " + totalDurationFormatted);

            holder.savedOn.setText("Last Played: " + currentBook.getLastPlayedDateTime());
            Picasso.get()
                    .load(currentBook.getImage()) // Ensure this method exists in AudioBook class
                    // Optional: Shows a placeholder while loading
                    // Optional: Shows an error image if the URL fails
                    .into(holder.bookImage);
            // You can set an image if you have an image URL or resource
            // Example: holder.bookImage.setImageResource(R.drawable.book_image);
            // Example: Glide or Picasso can be used to load the image if it's from a URL
            holder.itemView.setOnLongClickListener(v -> {
                // Inflate the custom layout
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_cusom, null);
                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message); // Assuming your TextView has id dialog_message

                // Set the message dynamically
                dialogMessage.setText("Remove your book history for " + currentBook.getTitle());
                // Create the dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(dialogView)
                        .setTitle("Delete Audiobook")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle deletion logic here
                                // Remove the audiobook from the list
                                File jsonFile = new File(v.getContext().getFilesDir(), "audiobook_info.json"); // Use the app's internal storage
                                String jsonString = readJsonFromFile(jsonFile);

                                // 2. Parse the JSON into a JSONArray
                                try {
                                    JSONArray bookArray = new JSONArray(jsonString);

                                    // 3. Find and remove the book with the matching title
                                    for (int i = 0; i < bookArray.length(); i++) {
                                        JSONObject bookObject = bookArray.getJSONObject(i);
                                        String title = bookObject.getString("title");

                                        if (title.equals(currentBook.getTitle())) {
                                            bookArray.remove(i); // Remove the book from the JSONArray
                                            break; // Exit loop after deleting the book
                                        }
                                    }

                                    // 4. Write the updated JSONArray back to the JSON file
                                    String updatedJson = bookArray.toString();
                                    writeJsonToFile(updatedJson, jsonFile);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Remove the audiobook from the list in the adapter
                                myBooks.remove(position);
                                notifyItemRemoved(position);

                                // Optionally show a toast message
                                Toast.makeText(v.getContext(), "Audiobook deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null);

                // Show the dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Load the image into the ImageView using Picasso
                ImageView dialogImageView = dialogView.findViewById(R.id.dialog_image);
                Picasso.get()
                        .load(currentBook.getImage()) // Ensure this method exists in AudioBook class
                        .into(dialogImageView);  // Picasso will load the image into the ImageView

                return true; // Return true to indicate the long press has been handled
            });
        }
    private String readJsonFromFile(File file) {
        String jsonString = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonString = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
    private void writeJsonToFile(String json, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }}

        @Override
        public int getItemCount() {
            return myBooks.size();
        }
    }


