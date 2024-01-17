package com.example.madassignment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.madassignment.Complaint;
import com.example.madassignment.ComplaintViewHolder;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintViewHolder> {
    private List<Complaint> complaintList;
    private OnItemClickListener onItemClickListener;

    // Constructor to initialize the list
    public ComplaintAdapter(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_complaint_list, parent, false);
        ComplaintViewHolder viewHolder = new ComplaintViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);

        holder.tvDate.setText(complaint.getDate());
        holder.tvTime.setText(complaint.getTime());
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvDescription.setText(complaint.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void updateData(List<Complaint> newComplaintList) {
        complaintList = newComplaintList;
        notifyDataSetChanged();
    }

    public Complaint getComplaint(int position) {
        return complaintList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = (OnItemClickListener) onItemClickListener;
    }
}