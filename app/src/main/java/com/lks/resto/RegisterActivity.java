package com.lks.resto;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirmPassword;
    Button btnRegister;
    String username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Buat custom actionbar */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title = findViewById(R.id.tvTitle);
        title.setText("Register");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCheck();
            }
        });
    }

    private void registerCheck() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        if( username.isEmpty() || password.isEmpty()){
            pesanGagal("Username, Password & Confirm Password tidak boleh kosong.");
        } else if ( !password.equals(confirmPassword)){
            pesanGagal("Password tidak sama dengan Confirm Password.");
        } else {
            registerSend();
        }
    }

    private void registerSend() {
        String url = getString(R.string.url_api)+"/register";

        JSONObject param = new JSONObject();
        try {
            param.put("username",username)
                    .put("password",password);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("Success")){
                                pesanSuccess("Registrasi telah berhasil, silahkan kembali ke Login");
                            }
                        } catch ( JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pesanGagal("Ada kelasahan, silahkan gunakan Username lain.");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(jsonRequest);
    }

    private void pesanSuccess(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(RegisterActivity.this);
        alertBuild.setTitle("Berhasil Registrasi")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alert =  alertBuild.create();
        alert.show();
    }

    private void pesanGagal(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(RegisterActivity.this);
        alertBuild.setTitle("Gagal Registrasi")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert =  alertBuild.create();
        alert.show();
    }
}