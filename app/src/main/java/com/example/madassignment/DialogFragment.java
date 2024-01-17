package com.example.madassignment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogFragment extends Dialog implements View.OnClickListener {
    private TextView dialogText;
    private Button closeButton;

    public DialogFragment(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_after_submit);

        dialogText = findViewById(R.id.TVDialogText);
        closeButton = findViewById(R.id.BtnClose);

        closeButton.setOnClickListener(this);
    }

    public void setDialogText(String text) {
        if (dialogText != null) {
            dialogText.setText(text);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BtnClose) {
            dismiss();
        }
    }
}
