package com.anju.soundsaga;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyBookActivityViewHolder extends RecyclerView.ViewHolder {
    TextView bookTitle, authorName, chapterName, timeGone, savedOn;
    ImageView bookImage;

    public MyBookActivityViewHolder(View itemView) {
        super(itemView);

        // Initialize the views
        bookTitle = itemView.findViewById(R.id.bookTitle);
        authorName = itemView.findViewById(R.id.authorName);
        chapterName = itemView.findViewById(R.id.chapterName);
        timeGone = itemView.findViewById(R.id.timegone);
        savedOn = itemView.findViewById(R.id.savedon);
        bookImage = itemView.findViewById(R.id.imageView3);
    }
}
