package com.example.trackme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.trackme.R;
import com.example.trackme.adapter.HistoryAdapter;
import com.example.trackme.database.Record;
import com.example.trackme.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {
    private static final int TRACK_REQUEST_CODE = 0x01;
    private HistoryViewModel mHistoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
    }

    private void initView() {
        ImageButton btnRecord = findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(v -> startTrackActivity());

        RecyclerView recyclerView = findViewById(R.id.recyclerHistory);
        final HistoryAdapter adapter = new HistoryAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        updateList(adapter);
    }

    private void updateList(HistoryAdapter adapter) {
        mHistoryViewModel.getAllRecord().observe(this, records -> {
            if (records != null) {
                adapter.submitList(records);
            }
        });

    }

    private void startTrackActivity() {
        Intent intent = new Intent(this, TrackMeActivity.class);
        startActivityForResult(intent, TRACK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TRACK_REQUEST_CODE && resultCode == RESULT_OK) {
            Record record = new Record();
            mHistoryViewModel.insert(record);
        }
    }
}