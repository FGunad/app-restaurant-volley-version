package com.lks.resto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MenuRecycleAdapter menuRecycleAdapter;
    List<MenuModel> menuList;
    FloatingActionButton fbAdd;
    ActivityResultLauncher addActivity, editActivity;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView title = findViewById(R.id.tvTitle);
        title.setText("Menu");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        localStorage = new LocalStorage(MenuActivity.this);

        recyclerView = findViewById(R.id.rvMenu);
        menuList = new ArrayList<>();
        menuGetList();

        addActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                      if(result.getResultCode() == RESULT_OK){
                          Toast.makeText(MenuActivity.this, "Add Success", Toast.LENGTH_SHORT).show();
                          menuGetList();
                      }
                    }
                });

        fbAdd = findViewById(R.id.fbMenuAdd);
        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddActivity.class);
                addActivity.launch(intent);
            }
        });

        editActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK){
                            Toast.makeText(MenuActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                            menuGetList();
                        }
                    }
                });

    }

    private void menuGetList() {
        String url =  getString(R.string.url_api)+"/menu";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String id, name, description, price;
                            menuList.clear();
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject json = jsonArray.getJSONObject(i);
                                id = json.getString("id");
                                name = json.getString("name");
                                description = json.getString("description");
                                price = json.getString("price");
                                menuList.add(new MenuModel(id, name, description, price));
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                            menuRecycleAdapter =  new MenuRecycleAdapter(MenuActivity.this, menuList);
                            recyclerView.setAdapter(menuRecycleAdapter);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, "Galat", Toast.LENGTH_SHORT).show();
            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization","Bearer "+localStorage.getToken());
                return  headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MenuActivity.this);
        requestQueue.add(stringRequest);
    }

    public void editMenu(String id, String name, String description, String price) {
        //Toast.makeText(this, "Edit Menu : "+id, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MenuActivity.this, EditActivity.class);
        intent.putExtra("ID",id);
        intent.putExtra("NAME",name);
        intent.putExtra("DESCRIPTION",description);
        intent.putExtra("PRICE", price);
        editActivity.launch(intent);
    }

    public void menuDelete(String id) {
        String url = getString(R.string.url_api)+"/menu/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        menuGetList();
                        Toast.makeText(MenuActivity.this, "Delete Menu : "+id, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, "Gagal didelete", Toast.LENGTH_SHORT).show();
            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization","Bearer "+localStorage.getToken());
                return  headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MenuActivity.this);
        requestQueue.add(stringRequest);
    }
}