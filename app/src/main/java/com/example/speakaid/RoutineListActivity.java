package com.example.speakaid;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RoutineListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Routine> routineList;
    DBHelper db;
    RoutineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        setTitle("Mini Routines");
        recyclerView = findViewById(R.id.recyclerViewRoutines);
        db = new DBHelper(this);

        loadRoutines();

        //for back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadRoutines() {
        routineList = new ArrayList<>();
        
        try (Cursor cursor = db.getRoutines()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                routineList.add(new Routine(id, title));
            }
        }

        adapter = new RoutineAdapter(routineList, routine -> {
            Intent intent = new Intent(this, RoutinePlayerActivity.class);
            intent.putExtra("routineId", routine.id);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadRoutines();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
