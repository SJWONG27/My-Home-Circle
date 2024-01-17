package com.example.madassignment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaViewHolder> {
    private List<residentialArea> dataList;
    private OnItemClickListener onItemClickListener;

    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public AreaAdapter(List<residentialArea> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arealist, parent, false);
        AreaViewHolder viewHolder = new AreaViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        residentialArea area = dataList.get(position);

        holder.tvName.setText(area.getName());
        holder.tvCode.setText(area.getCode());
        loadAndDisplayPhoto(area.getPhotoLink(), holder.ivPhoto);

        if (position == selectedItemPosition) {
            // Change the background color of the clicked item
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.selectedyellow));
        } else {
            // Reset the background color for unselected items
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // or set the default color
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    // Update the selected item position
                    selectedItemPosition = position;
                    notifyDataSetChanged(); // Notify the adapter that the data set changed

                    // Call the onItemClick method
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    private void loadAndDisplayPhoto(String storagePath, ImageView imageView) {
        // Get Firebase Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storagePath);

        // Get the download URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Picasso with the HTTP/HTTPS URL
            Picasso.get().load(uri.toString()).into(imageView, new Callback() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }).addOnFailureListener(exception -> {
            // Handle failure
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setResidentialAreas(List<residentialArea> residentialAreas) {
        dataList = residentialAreas;
        notifyDataSetChanged();
    }

    // Setter method for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public residentialArea getResidentialArea(int position) {
        return dataList.get(position);
    }
}
