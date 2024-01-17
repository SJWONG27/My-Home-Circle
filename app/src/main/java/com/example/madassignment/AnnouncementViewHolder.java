package com.example.madassignment;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
    public TextView tvDay, tvDate, tvTime, tvTitle, tvDescription;

    public AnnouncementViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        tvDay = itemView.findViewById(R.id.TVday);
        tvDate = itemView.findViewById(R.id.TVdate);
        tvTime = itemView.findViewById(R.id.TVtime);
        tvTitle = itemView.findViewById(R.id.TVAnnTitle);
        tvDescription = itemView.findViewById(R.id.TVAnnDes);

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(position);
            }
        });
    }
}
