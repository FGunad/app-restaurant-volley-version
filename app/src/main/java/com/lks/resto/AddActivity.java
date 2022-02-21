package com.lks.resto;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText etName, etDescription, etPrice;
    Button btnAdd;
    String name, description, price;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title = findViewById(R.id.tvTitle);
        title.setText("Menu Detail");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        localStorage = new LocalStorage(AddActivity.this);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCheck();
            }
        });

    }

    private void addCheck() {
        name = etName.getText().toString();
        description =  etDescription.getText().toString();
        price = etPrice.getText().toString();

        if(name.isEmpty() || description.isEmpty() || price.isEmpty()){
            pesanGagal("Menu Name, Description & Price tidak boleh kosong.");
        } else {
            addSend();
        }
    }

    private void addSend() {
        String url = getString(R.string.url_api)+"/menu";
        JSONObject param = new JSONObject();
        try {
            param.put("name", name);
            param.put("description", description);
            param.put("price", price);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if(status.equals("Success")){
                                  Intent intent = new Intent();
                                  setResult(RESULT_OK, intent);
                                  finish();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pesanGagal("Ada kesalahan, periksa kembali koneksi.");
            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization","Bearer "+localStorage.getToken());
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(AddActivity.this);
        requestQueue.add(jsonRequest);
    }

    private void pesanGagal(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(AddActivity.this);
        alertBuild.setTitle("Gagal Tambah Menu")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertBuild.create();
        alert.show();
    }
}