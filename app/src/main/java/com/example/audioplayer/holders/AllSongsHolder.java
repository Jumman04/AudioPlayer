package com.example.audioplayer.holders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.audioplayer.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

public class AllSongsHolder extends ViewHolder {


    public final ShapeableImageView song_image;
    public final ImageView three_dots;
    public final TextView player_song_name;
    public final TextView player_artist_name;
    final float radius = 10f;


    public AllSongsHolder(@NonNull View itemView) {
        super(itemView);

        song_image = itemView.findViewById(R.id.song_image);
        three_dots = itemView.findViewById(R.id.three_dots);
        player_song_name = itemView.findViewById(R.id.player_song_name);
        player_artist_name = itemView.findViewById(R.id.player_artist_name);

        SHAPED_IMAGE_VIEW();


    }


    public void SHAPED_IMAGE_VIEW() {
        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder().setTopLeftCorner(CornerFamily.ROUNDED, radius).setBottomLeftCorner(CornerFamily.ROUNDED, radius).setBottomRightCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build();
        song_image.setShapeAppearanceModel(shapeAppearanceModel);
    }


}
