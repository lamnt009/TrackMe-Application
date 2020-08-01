package com.example.trackme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

import com.example.trackme.R;
import com.example.trackme.adapter.HistoryAdapter;
import com.example.trackme.model.Record;
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
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        ImageButton btnRecord = findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(v -> startNewRecord());


        final HistoryAdapter adapter = new HistoryAdapter(this, mHistoryViewModel);

        RecyclerView recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        updateList(adapter, recyclerView, linearLayoutManager);
        mHistoryViewModel.getSelectRecord().observe(this, new Observer<Record>() {
            @Override
            public void onChanged(Record record) {
                if (record != null)
                startReviewRecord(record);
            }
        });
    }

    private void updateList(HistoryAdapter adapter, RecyclerView view, LinearLayoutManager manager) {
        mHistoryViewModel.getAllRecord().observe(this, records -> {
            if (records != null) {
                adapter.submitList(records);
                new Handler().postDelayed(() -> manager.smoothScrollToPosition(view, null, 0), 2000);
            }
        });

    }

    private void startNewRecord() {
        Intent intent = new Intent(this, TrackMeActivity.class);
        startActivityForResult(intent, TRACK_REQUEST_CODE);
    }

    private void startReviewRecord(Record record) {
        Intent intent = new Intent(this, TrackMeActivity.class);
        intent.putExtra(Constants.INTENT_KEY.SEASON_KEY, record);
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