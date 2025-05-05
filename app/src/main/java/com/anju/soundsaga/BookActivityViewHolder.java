package com.anju.soundsaga;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView chapterImage;


        public BookActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterImage = itemView.findViewById(R.id.imageView4);
        }
    }


