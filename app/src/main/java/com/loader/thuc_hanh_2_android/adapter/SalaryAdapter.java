package com.loader.thuc_hanh_2_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;

import java.util.List;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    // Khi P3 tạo xong class Salary thì đổi String thành Salary
    private List<String> salaryList;

    public SalaryAdapter(List<String> salaryList) {
        this.salaryList = salaryList;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        String data = salaryList.get(position);
        holder.tvMonthYear.setText("Tháng: " + data);
    }

    @Override
    public int getItemCount() {
        return salaryList != null ? salaryList.size() : 0;
    }

    static class SalaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonthYear, tvStatus, tvTotalHours, tvOvertime, tvTotalAmount;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonthYear = itemView.findViewById(R.id.tv_salary_month_year);
            tvStatus = itemView.findViewById(R.id.tv_salary_status);
            tvTotalHours = itemView.findViewById(R.id.tv_salary_total_hours);
            tvOvertime = itemView.findViewById(R.id.tv_salary_overtime);
            tvTotalAmount = itemView.findViewById(R.id.tv_salary_total_amount);
        }
    }
}