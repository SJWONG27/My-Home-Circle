package com.example.madassignment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ComplaintViewHolder extends RecyclerView.ViewHolder {
    TextView tvDate, tvTime, tvTitle, tvDescription;

    public ComplaintViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.TVdate);
        tvTime = itemView.findViewById(R.id.TVtime);
        tvTitle = itemView.findViewById(R.id.TVAnnTitle);
        tvDescription = itemView.findViewById(R.id.TVAnnDes);

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null)
                onItemClickListener.onItemClick(position);
        });
    }
}