package com.example.madassignment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AreaViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvCode;
    public ImageView ivPhoto;

    public AreaViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        tvName = itemView.findViewById(R.id.TVname1);
        tvCode = itemView.findViewById(R.id.TVcode1);
        ivPhoto = itemView.findViewById(R.id.IV1);

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(position);
            }
        });
    }
}
