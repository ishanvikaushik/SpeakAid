package com.example.speakaid;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScriptListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Script> scriptList;
    ScriptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_list);

        setTitle("Social Practice");

        //  Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerScripts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scriptList = new ArrayList<>();

        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getScripts();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);

            scriptList.add(new Script(id, title));
        }

        adapter = new ScriptAdapter(this, scriptList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}