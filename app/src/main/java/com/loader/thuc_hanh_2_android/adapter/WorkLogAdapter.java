package com.loader.thuc_hanh_2_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;

import java.util.List;

public class WorkLogAdapter extends RecyclerView.Adapter<WorkLogAdapter.WorkLogViewHolder> {

    // Khi P2 tạo xong class WorkLog thì đổi String thành WorkLog
    private List<String> workLogList;

    public WorkLogAdapter(List<String> workLogList) {
        this.workLogList = workLogList;
    }

    @NonNull
    @Override
    public WorkLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worklog, parent, false);
        return new WorkLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkLogViewHolder holder, int position) {
        String data = workLogList.get(position);
        holder.tvDate.setText("Ngày: " + data);
    }

    @Override
    public int getItemCount() {
        return workLogList != null ? workLogList.size() : 0;
    }

    static class WorkLogViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvIn, tvOut, tvTotal, tvStatus;

        public WorkLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_work_date);
            tvIn = itemView.findViewById(R.id.tv_check_in_time);
            tvOut = itemView.findViewById(R.id.tv_check_out_time);
            tvTotal = itemView.findViewById(R.id.tv_total_hours);
            tvStatus = itemView.findViewById(R.id.tv_work_status);
        }
    }
}