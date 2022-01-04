package com.example.mo_v.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mo_v.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

   //Widgets

   ImageView movieimg;
   TextView title, release_date, duration;
   RatingBar ratingBar;

   OmMovieListener omMovieListener;

    public MovieViewHolder(@NonNull View itemView, OmMovieListener omMovieListener) {
        super(itemView);

        this.omMovieListener=omMovieListener;
        movieimg=itemView.findViewById(R.id.movie_image);
        title=itemView.findViewById(R.id.movie_title);
        release_date=itemView.findViewById(R.id.movie_category);
        duration=itemView.findViewById(R.id.movie_duration);
        ratingBar=itemView.findViewById(R.id.rating_bar);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
            omMovieListener.onMovieClick(getAdapterPosition());
    }
}