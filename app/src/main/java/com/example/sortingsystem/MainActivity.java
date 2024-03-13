package com.example.sortingsystem;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
//Xzing
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//web
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton scanBtn;
    ImageButton scanBtn2;
    TextView messageText;
    TextView amountEditText;

    // Variables to store temporary values
    private String scannedId;  // To store the ID after the first scan
    private String scannedAmount;
    private boolean isScanBtn2Clicked = false;

    @Override
    public void onClick(View v) {
             // Implement the onClick method based on your needs
        if (v.getId() == R.id.scanBtn) {
            // Handle click for scanBtn
            isScanBtn2Clicked = false;  // Reset the flag
            startQrCodeScan();
        } else if (v.getId() == R.id.scanBtn2) {
            // Handle click for scanBtn2
            isScanBtn2Clicked = true;  // Reset the flag
            String amount = amountEditText.getText().toString();
            startQrCodeScan();
        }
        // Add more conditions for other clickable views if needed
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn2 = findViewById(R.id.scanBtn2);
        amountEditText = findViewById(R.id.amount);
        messageText = findViewById(R.id.textContent);
        /*
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for the first button (e.g., initiate QR code scan)
                startQrCodeScan();
            }
        });

        scanBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for the second button (e.g., get information from the web and append to web)
                // Get the amount from the amountEditText
                String amount = amountEditText.getText().toString();
                // Start QR code scan
                startQrCodeScan();
            }
        });*/
        scanBtn.setOnClickListener(this);
        scanBtn2.setOnClickListener(this);
    }
    //Method to initiate QR code scan
    private void startQrCodeScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // messageText.setText(intentResult.getContents());

                // Check which button was clicked
                if (requestCode == IntentIntegrator.REQUEST_CODE) {
                    if (isScanBtn2Clicked) {
                        // Get the amount from the amountEditText
                        String amount = amountEditText.getText().toString();

                        // Make a GET request to append information to the web based on the scanned ID and amount
                        appendToWeb(intentResult.getContents(), amount);
                    } else {
                        // Perform the action for the first button if needed
                        getFromWeb(intentResult.getContents());
                    }
                }
            }
        }
    }

    private void getFromWeb(String id){
        // Replace the URL with your actual URL
        String url = "https://script.google.com/macros/s/AKfycbzwqJ3_-LeXmhh8pAd6cf_fz8J-vJnZu_DUTP91T5R0ENxFmuiWfY0Y3A3GBoDI0-Q/exec?action=get&id=" + id;

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        Log.d("VolleyResponse", response);

                        // You can parse the response JSON or handle it as needed
                        // Update your UI or perform other actions based on the response
                        // For example, you can set the response in your messageText TextView
                        messageText.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("VolleyError", "Error: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Error in GET request", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void appendToWeb(String  id, String quantity) {
         // Replace the URL with your actual URL
        String url = "https://script.google.com/macros/s/AKfycbzwqJ3_-LeXmhh8pAd6cf_fz8J-vJnZu_DUTP91T5R0ENxFmuiWfY0Y3A3GBoDI0-Q/exec?action=edit&id=" + id + "&amount=" + quantity;

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        Log.d("VolleyResponse", response);

                        // You can parse the response JSON or handle it as needed
                        // Update your UI or perform other actions based on the response
                        messageText.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("VolleyError", "Error: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Error in GET request", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}