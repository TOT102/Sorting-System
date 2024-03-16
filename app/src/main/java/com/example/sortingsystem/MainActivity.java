package com.example.sortingsystem;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton scanBtn; // Used for obtaining the quantity for a given ID
    ImageButton scanBtn2; // Used for modifying the quantity for a given ID
    TextView messageText; // Used for visualizing the result from ^
    TextView amountEditText; // Used for obtaining the quantity to be modified
    private boolean isScanBtn2Clicked = false; // Flag variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn2 = findViewById(R.id.scanBtn2);
        amountEditText = findViewById(R.id.amount);
        messageText = findViewById(R.id.textContent);

        scanBtn.setOnClickListener(this);
        scanBtn2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanBtn) {
            isScanBtn2Clicked = false;
            startQrCodeScan();
        } else if (v.getId() == R.id.scanBtn2) {
            isScanBtn2Clicked = true;
            //String amount = amountEditText.getText().toString();
            startQrCodeScan();
        }
    }

    private void startQrCodeScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Executes after the button is released and the QR code is scanned
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                if (requestCode == IntentIntegrator.REQUEST_CODE) {
                    if (isScanBtn2Clicked) {
                        String amount = amountEditText.getText().toString();

                        appendToWeb(intentResult.getContents(), amount);
                    } else {
                        getFromWeb(intentResult.getContents());
                    }
                }
            }
        }
    }

    private void getFromWeb(String id){
        String url = "https://script.google.com/macros/s/AKfycbzwqJ3_-LeXmhh8pAd6cf_fz8J-vJnZu_DUTP91T5R0ENxFmuiWfY0Y3A3GBoDI0-Q/exec?action=get&id=" + id;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle the response
                    Log.d("VolleyResponse", response);

                    // You can parse the response JSON or handle it as needed
                    // Update your UI or perform other actions based on the response
                    // For example, you can set the response in your messageText TextView
                    messageText.setText(response);
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error in GET request", Toast.LENGTH_SHORT).show();
                });


        queue.add(stringRequest);
    }

    private void appendToWeb(String  id, String quantity) {
         // Replace the URL with your actual URL
        String url = "https://script.google.com/macros/s/AKfycbzwqJ3_-LeXmhh8pAd6cf_fz8J-vJnZu_DUTP91T5R0ENxFmuiWfY0Y3A3GBoDI0-Q/exec?action=edit&id=" + id + "&amount=" + quantity;

        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("VolleyResponse", response);

                    messageText.setText(response);
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error in GET request", Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }
}