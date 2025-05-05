package com.anju.soundsaga;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AudioBookViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView Author;
    ImageView image;

    public AudioBookViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.textView3);
        Author = itemView.findViewById(R.id.textView4);
        image = itemView.findViewById(R.id.imageView2);
    }

}
