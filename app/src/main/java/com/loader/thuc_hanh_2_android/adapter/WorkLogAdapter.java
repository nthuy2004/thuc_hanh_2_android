package com.loader.thuc_hanh_2_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.databinding.ItemWorklogBinding;
import com.loader.thuc_hanh_2_android.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hien thi danh sach nhat ky cham cong theo item_worklog.xml.
 */
public class WorkLogAdapter extends RecyclerView.Adapter<WorkLogAdapter.WorkLogViewHolder> {

    private final List<WorkLog> workLogs = new ArrayList<>();

    public void setWorkLogs(List<WorkLog> logs) {
        this.workLogs.clear();
        if (logs != null) {
            this.workLogs.addAll(logs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorklogBinding binding = ItemWorklogBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new WorkLogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkLogViewHolder holder, int position) {
        holder.bind(workLogs.get(position));
    }

    @Override
    public int getItemCount() {
        return workLogs.size();
    }

    static class WorkLogViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorklogBinding binding;

        public WorkLogViewHolder(@NonNull ItemWorklogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WorkLog log) {
            Context context = itemView.getContext();

            binding.tvWorkDate.setText("Ngày: " + log.workDate);

            String inTime = log.checkInTime != null ? log.checkInTime : "--:--:--";
            String outTime = log.checkOutTime != null ? log.checkOutTime : "--:--:--";
            binding.tvCheckInTime.setText("Vào: " + inTime);
            binding.tvCheckOutTime.setText("Ra: " + outTime);

            double ot = TimeUtils.calculateDailyOvertime(log.totalHours);
            if (ot > 0 && WorkLog.STATUS_COMPLETED.equalsIgnoreCase(log.status)) {
                binding.tvTotalHours.setText(String.format(Locale.US, "Tổng: %.2fh (OT: %.2fh)", log.totalHours, ot));
            } else {
                binding.tvTotalHours.setText(String.format(Locale.US, "Tổng: %.2fh", log.totalHours));
            }

            String status = log.status != null ? log.status : WorkLog.STATUS_INCOMPLETE;
            binding.tvWorkStatus.setText(status);

            if (WorkLog.STATUS_COMPLETED.equalsIgnoreCase(status)) {
                binding.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.secondaryColor));
            } else if (WorkLog.STATUS_WORKING.equalsIgnoreCase(status)) {
                binding.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
            } else {
                binding.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.errorColor));
            }
        }
    }
}
