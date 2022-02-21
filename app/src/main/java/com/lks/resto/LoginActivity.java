package com.lks.resto;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;
    String username, password;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Buat custom actionbar */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title = findViewById(R.id.tvTitle);
        title.setText("Login");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(LoginActivity.this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginCheck() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            pesanGagal("Username & Password tidak boleh kosong.");
        } else {
            loginSend();
        }
    }

    private void loginSend() {

        String url = getString(R.string.url_api)+"/login";
        JSONObject param  = new JSONObject();
        try {
            param.put("username",username);
            param.put("password",password);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String token = response.getString("token");
                            if(status.equals("Success")){
                                localStorage.setToken(token);
                                etUsername.setText("");
                                etPassword.setText("");
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pesanGagal("Username dan Password anda masukan salah.");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(jsonRequest);
    }

    private void pesanGagal(String msg) {
        AlertDialog.Builder alertBuild =  new AlertDialog.Builder(LoginActivity.this);
        alertBuild.setTitle("Gagal Login")
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