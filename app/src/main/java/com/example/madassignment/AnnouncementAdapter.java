package com.example.madassignment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementViewHolder> {
    private List<Announcement> dataList;
    private OnItemClickListener onItemClickListener;

    public AnnouncementAdapter(List<Announcement> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.annucementlist, parent, false);
        AnnouncementViewHolder viewHolder = new AnnouncementViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Announcement announcement = dataList.get(position);

        holder.tvDay.setText(announcement.getDay());
        holder.tvDate.setText(announcement.getDate());
        holder.tvTime.setText(announcement.getTime());
        holder.tvTitle.setText(announcement.getTitle());
        holder.tvDescription.setText(announcement.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setAnnouncements(List<Announcement> announcements) {
        dataList = announcements;
        notifyDataSetChanged();
    }

    // Setter method for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public Announcement getAnnouncement(int position) {
        return dataList.get(position);
    }

}
