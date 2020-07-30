package com.example.trackme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.trackme.R;
import com.example.trackme.database.Record;
import com.example.trackme.viewholder.HistoryViewHolder;

public class HistoryAdapter extends PagedListAdapter<Record, HistoryViewHolder> {
    private Context mContext;
    public HistoryAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        Record record = getItem(position);
        if (record != null)
            holder.updateView(record, mContext);

    }

    private static DiffUtil.ItemCallback<Record> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Record>() {
                @Override
                public boolean areItemsTheSame(Record oldConcert, Record newConcert) {
                    return oldConcert.getRecordId().equals(newConcert.getRecordId());
                }

                @Override
                public boolean areContentsTheSame(Record oldConcert,
                                                  Record newConcert) {
                    return oldConcert.toString().equals(newConcert.toString());
                }
            };
}
