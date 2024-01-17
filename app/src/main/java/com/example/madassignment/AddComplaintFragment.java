package com.example.madassignment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AddComplaintFragment extends Fragment {

    private Calendar calendar;
    private FirebaseFirestore firestore;
    private EditText nameEditText, titleEditText, photoTitleEditText, descriptionEditText;
    private TextView dateEditText, timeEditText;
    private String photoUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_complaint, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        nameEditText = view.findViewById(R.id.ETName);
        titleEditText = view.findViewById(R.id.ETTitle);
        photoTitleEditText = view.findViewById(R.id.ETPhotoTitle);
        descriptionEditText = view.findViewById(R.id.ETDescription);
        dateEditText = view.findViewById(R.id.TVDateInput);
        timeEditText = view.findViewById(R.id.TVTimeInput);

        FloatingActionButton uploadPhotoButton = view.findViewById(R.id.BtnUploadPhoto);

        uploadPhotoButton.setOnClickListener(v -> openPhotoPicker());

        Button submitComplaintButton = view.findViewById(R.id.BtnSubmit);
        submitComplaintButton.setOnClickListener(v -> saveToFirebase());

        TextView chooseDate = view.findViewById(R.id.TVDateInput);
        TextView chooseTime = view.findViewById(R.id.TVTimeInput);
        calendar = Calendar.getInstance();

        chooseDate.setClickable(true);
        chooseDate.setOnClickListener(v -> showDatePickerDialog());

        chooseTime.setClickable(true);
        chooseTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void openPhotoPicker() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == -1 && data != null) {
            Uri selectedImageUri = data.getData();
            uploadImageAndSaveToFirestore(selectedImageUri);
        }
    }

    private void uploadImageAndSaveToFirestore(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        photoUrl = uri.toString();
                    });
                    Toast.makeText(getContext(), "Upload photo successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorageError", "Failed to upload image", e);
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFirebase() {
        String name = nameEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String photoTitle = photoTitleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();

        if (name.isEmpty() || title.isEmpty() || photoTitle.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            saveDataToFirestore();
            showDialogAfterSubmit();
        }
    }

    private void saveDataToFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String name = nameEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String photoTitle = photoTitleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();


        int rand = new Random().nextInt(1000);
        String ticketNo = Integer.toString(rand);

        String[] progress = {"inprogress", "completed"};

        Map<String, Object> complaint = new HashMap<>();
        complaint.put("name", name);
        complaint.put("title", title);
        complaint.put("photoTitle", photoTitle);
        complaint.put("description", description);
        complaint.put("date", date);
        complaint.put("time", time);
        complaint.put("photoUrl", photoUrl);
        complaint.put("ticketNo", ticketNo);
        complaint.put("category", progress);

        firestore.collection("complaint").add(complaint)
                .addOnSuccessListener(documentReference -> {
                    nameEditText.getText().clear();
                    titleEditText.getText().clear();
                    photoTitleEditText.getText().clear();
                    descriptionEditText.getText().clear();
                    dateEditText.setText("");
                    timeEditText.setText("");
                    returnAfterSubmitComplaint();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to submit complaint", e);
                    Toast.makeText(getContext(), "Failed to submit complaint", Toast.LENGTH_SHORT).show();
                });
    }

    private void returnAfterSubmitComplaint() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void showDialogAfterSubmit() {
        DialogFragment dialog = new DialogFragment(getContext());
        dialog.setDialogText("Feedback submitted. Thank You");
        dialog.show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                    dateEditText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    timeEditText.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
}