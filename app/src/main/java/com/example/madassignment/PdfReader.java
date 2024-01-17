//package com.example.madassignment;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.fragment.app.Fragment;
//import com.github.barteksc.pdfviewer.PDFView;
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class PdfReader extends Fragment {
//    private PDFView pdfView;
//    private String pdfUrl; // URL of the PDF to be displayed
//
//    public PdfReader() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
//        pdfView = view.findViewById(R.id.pdfView);
//
//        // Load PDF from URL
//        if (pdfUrl != null) {
//            displayFromUrl(pdfUrl);
//        }
//
//        return view;
//    }
//
//    public void setPdfUrl(String pdfUrl) {
//        this.pdfUrl = pdfUrl;
//    }
//
//    private void displayFromUrl(String url) {
//        // Use AsyncTask or any other mechanism to load PDF from the given URL
//        new PdfDownloadTask().execute(url);
//    }
//
//    private class PdfDownloadTask extends AsyncTask<String, Void, InputStream> {
//        @Override
//        protected InputStream doInBackground(String... strings) {
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                return inputStream;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(InputStream inputStream) {
//            if (inputStream != null) {
//                pdfView.fromStream(inputStream).load();
//            }
//        }
//    }
//}
