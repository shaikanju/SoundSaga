package com.anju.soundsaga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookActivityAdapter extends RecyclerView.Adapter<BookActivityViewHolder> {
    private List<Chapter> chapterList;
    private Context context;
    private String imageurl;

    public BookActivityAdapter(String imageurl,List<Chapter> chapterList, Context context) {
        this.chapterList = chapterList;
        this.context = context;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public BookActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chapter, parent, false);
        return new BookActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookActivityViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);

        Picasso.get()
                .load(imageurl) // Ensure this method exists in AudioBook class
                // Optional: Shows a placeholder while loading
                // Optional: Shows an error image if the URL fails
                .into(holder.chapterImage);
         // Assuming local image resource
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }
}

