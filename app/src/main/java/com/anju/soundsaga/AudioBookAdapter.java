package com.anju.soundsaga;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anju.soundsaga.databinding.DialogBookDetailsBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AudioBookAdapter extends RecyclerView.Adapter<AudioBookViewHolder> {

    private final MainActivity mainActivity;
    private final List<AudioBook> AudioBooks;

    public AudioBookAdapter(MainActivity mainActivity, List<AudioBook> AudioBooks) {
        this.mainActivity = mainActivity;
        this.AudioBooks = AudioBooks;
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);


        return new AudioBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioBookViewHolder holder, int position) {
        AudioBook audioBook = AudioBooks.get(position);
        holder.title.setText(audioBook.getTitle());
        holder.Author.setText(audioBook.getAuthor());
        holder.title.setSelected(true);
        holder.Author.setSelected(true);
        Picasso.get()
                .load(audioBook.getImage()) // Ensure this method exists in AudioBook class
               // Optional: Shows a placeholder while loading
                 // Optional: Shows an error image if the URL fails
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AudioBookActivity.class);
            intent.putExtra("audioBook", audioBook);
            intent.putExtra("source", "MainActivity");// Pass the AudioBook object
            v.getContext().startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            showBookDetailsDialog(v.getContext(), audioBook);
            return true; // Return true to indicate the event was handled
        });

    }
    private void showBookDetailsDialog(Context context, AudioBook audioBook) {
        // Inflate the dialog layout using View Binding
        DialogBookDetailsBinding binding = DialogBookDetailsBinding.inflate(LayoutInflater.from(context));

        // Set Data using View Binding
        Picasso.get().load(audioBook.getImage()).into(binding.bookImage);
        binding.bookTitle.setText(audioBook.getTitle()+" ("+ audioBook.getDate()+") ");
        binding.bookAuthor.setText(audioBook.getAuthor());
        binding.bookDate.setText(audioBook.getDate());
        binding.bookLanguage.setText("Language: "+audioBook.getLanguage());
        binding.bookDuration.setText("Duration: " +audioBook.getDuration());
        binding.bookChapters.setText(audioBook.getContents().size()+" Chapters ");

        // Set up Dialog with the bound view
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(binding.getRoot());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#800000"));
        });

        // Set up OK Button Click
//        binding.okButton.setOnClickListener(v -> dialog.dismiss());

        // Show Dialog
        dialog.show();
    }



    @Override
    public int getItemCount() {
        return AudioBooks.size();
    }
}
