package com.example.speakaid;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoutineListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Routine> routineList;
    DBHelper db;
    RoutineAdapter adapter;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        recyclerView = findViewById(R.id.recyclerViewRoutines);
        btnBack = findViewById(R.id.btnBack); // Need to add this to XML
        db = new DBHelper(this);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadRoutines();
    }

    private void loadRoutines() {
        routineList = new ArrayList<>();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        try (Cursor cursor = db.getRoutines()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int lastStep = cursor.getInt(2);
                String completedDate = cursor.getString(3);

                if (completedDate != null && !completedDate.equals(today)) {
                    db.resetRoutineCompletion(id);
                    completedDate = null;
                }

                routineList.add(new Routine(id, title, lastStep, completedDate));
            }
        }

        adapter = new RoutineAdapter(routineList, db, routine -> {
            if (routine.completedDate == null) {
                Intent intent = new Intent(this, RoutinePlayerActivity.class);
                intent.putExtra("routineId", routine.id);
                intent.putExtra("startStep", routine.lastStep);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutines();
    }
}
