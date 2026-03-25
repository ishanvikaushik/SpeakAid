package com.example.speakaid;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResetProgressActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Routine> routineList;
    Set<Integer> selectedRoutines = new HashSet<>();
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_progress);

        db = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerReset);
        MaterialButton btnApply = findViewById(R.id.btnApplyReset);

        loadRoutines();

        btnApply.setOnClickListener(v -> {
            for (Integer id : selectedRoutines) {
                db.updateRoutineProgress(id, 0);
                db.resetRoutineCompletion(id);
            }
            Toast.makeText(this, "Reset " + selectedRoutines.size() + " routines", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadRoutines() {
        routineList = new ArrayList<>();
        try (Cursor cursor = db.getRoutines()) {
            while (cursor.moveToNext()) {
                routineList.add(new Routine(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ResetAdapter());
    }

    private class ResetAdapter extends RecyclerView.Adapter<ResetAdapter.ViewHolder> {
        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox chk;
            TextView txt;
            ViewHolder(View v) { super(v); chk = v.findViewById(R.id.chkReset); txt = v.findViewById(R.id.txtRoutineName); }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vT) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_reset, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Routine r = routineList.get(pos);
            h.txt.setText(r.title);
            h.chk.setOnCheckedChangeListener(null);
            h.chk.setChecked(selectedRoutines.contains(r.id));
            h.chk.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) selectedRoutines.add(r.id); else selectedRoutines.remove(r.id);
            });
        }

        @Override
        public int getItemCount() { return routineList.size(); }
    }
}
