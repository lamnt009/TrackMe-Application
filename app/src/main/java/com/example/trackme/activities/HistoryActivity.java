package com.example.trackme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.trackme.R;
import com.example.trackme.adapter.HistoryAdapter;
import com.example.trackme.database.Record;
import com.example.trackme.utils.Constants;
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
        btnRecord.setOnClickListener(v -> startNewRecord());

        RecyclerView recyclerView = findViewById(R.id.recyclerHistory);
        final HistoryAdapter adapter = new HistoryAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        updateList(adapter, recyclerView);
    }

    private void updateList(HistoryAdapter adapter, RecyclerView view) {
        mHistoryViewModel.getAllRecord().observe(this, records -> {
            if (records != null) {
                adapter.submitList(records);
                view.smoothScrollBy(0, 0);
            }
        });

    }

    private void startNewRecord() {
        Intent intent = new Intent(this, TrackMeActivity.class);
        startActivityForResult(intent, TRACK_REQUEST_CODE);
    }

    private void startReviewRecord(String recordID) {
        Intent intent = new Intent(this, TrackMeActivity.class);
        intent.putExtra(Constants.INTENT_KEY.SEASON_KEY, recordID);
        startActivityForResult(intent, TRACK_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TRACK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Record record = (Record) data.getSerializableExtra(Constants.INTENT_KEY.RECORD_RESULT_KEY);
            mHistoryViewModel.insert(record);
        }
    }
}