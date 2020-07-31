package com.example.trackme.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trackme.R;
import com.example.trackme.model.Record;
import com.example.trackme.utils.DateUtils;
import com.example.trackme.utils.FileUtil;
import com.example.trackme.utils.Utils;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgMapRoute;
    private TextView tvDistance;
    private TextView tvAvgSpeed;
    private TextView tvDuration;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        imgMapRoute = itemView.findViewById(R.id.imgItemHistoryMapRoute);
        tvDistance = itemView.findViewById(R.id.tvItemHistoryDistance);
        tvAvgSpeed = itemView.findViewById(R.id.tvItemHistorySpeed);
        tvDuration = itemView.findViewById(R.id.tvItemHistoryDuration);
    }

    public void updateView(Record record, Context context) {
        Glide.with(context).load(FileUtil.getFileUri(context, record.getMapImageName())).placeholder(context.getDrawable(R.drawable.ic_broken_image)).into(imgMapRoute);
        tvDistance.setText(String.format(context.getString(R.string.distane_format), record.getDistance() / 1000));
        tvAvgSpeed.setText(String.format(context.getString(R.string.avg_speed_format), Utils.avgSpeedKmH(record.getAvgSpeed())));
        tvDuration.setText(DateUtils.convertMinToHH(record.getDuration()));
    }
}
