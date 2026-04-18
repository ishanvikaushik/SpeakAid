package com.example.speakaid;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ScriptListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Script> scriptList;
    DBHelper db;
    ScriptAdapter adapter;
    ImageView btnBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_list);

        recyclerView = findViewById(R.id.recyclerScripts);
        btnBack = findViewById(R.id.btnBack);
        db = new DBHelper(this);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadScripts();
    }

    private void loadScripts() {
        scriptList = new ArrayList<>();
        try (android.database.Cursor cursor = db.getScripts()) {
            while (cursor.moveToNext()) {
                scriptList.add(new Script(cursor.getInt(0), cursor.getString(1)));
            }
        }

        adapter = new ScriptAdapter(this, scriptList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
