package com.example.speakaid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RoutineListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Routine> routineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        recyclerView = findViewById(R.id.recyclerViewRoutines);

        routineList = new ArrayList<>();

        // Dummy data
        routineList.add(new Routine(1, "Morning Routine"));
        routineList.add(new Routine(2, "School Routine"));
        routineList.add(new Routine(3, "Bedtime Routine"));

        RoutineAdapter adapter = new RoutineAdapter(routineList, routine -> {
            Intent intent = new Intent(this, RoutinePlayerActivity.class);
            intent.putExtra("routineId", routine.id);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}