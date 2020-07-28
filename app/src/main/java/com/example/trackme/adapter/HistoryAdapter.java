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

import java.util.List;

public class HistoryAdapter extends PagedListAdapter<Record, HistoryViewHolder> {

    private final LayoutInflater mInflater;
    private List<Record> mRecord = null;
    private Context mContext;

    public HistoryAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (mRecord != null) {
            Record record = mRecord.get(position);
            holder.updateView(record, mContext);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecord == null)
            return 0;
        return mRecord.size();
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
                    return oldConcert.getRecordId().equals(newConcert.getRecordId());
                }
            };
}
