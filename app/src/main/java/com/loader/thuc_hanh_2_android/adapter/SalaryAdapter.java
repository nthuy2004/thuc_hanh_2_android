package com.loader.thuc_hanh_2_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.dao.EmployeeDao;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder> {

    public interface OnMarkPaidListener {
        void onMarkPaid(Salary salary);
    }

    private final List<Salary> salaries = new ArrayList<>();
    private final EmployeeDao employeeDao;
    private final boolean adminMode;
    private final OnMarkPaidListener listener;

    public SalaryAdapter(EmployeeDao employeeDao, boolean adminMode,
                         OnMarkPaidListener listener) {
        this.employeeDao = employeeDao;
        this.adminMode = adminMode;
        this.listener = listener;
    }

    public void setSalaries(List<Salary> data) {
        salaries.clear();
        if (data != null) {
            salaries.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Salary salary = salaries.get(position);

        holder.monthYear.setText(
                String.format(Locale.getDefault(), "Bảng lương %02d/%d",
                        salary.month, salary.year));

        holder.totalHours.setText(
                String.format(Locale.getDefault(), "Tổng giờ: %.2f giờ",
                        salary.totalHours));

        holder.overtime.setText(
                String.format(Locale.getDefault(), "Tăng ca: %.2f giờ",
                        salary.overtimeHours));

        NumberFormat currency = NumberFormat.getNumberInstance(
                Locale.forLanguageTag("vi-VN"));
        holder.amount.setText(currency.format(salary.totalSalary) + " VNĐ");

        boolean paid = Salary.STATUS_PAID.equals(salary.status);
        holder.status.setText(paid ? "ĐÃ THANH TOÁN" : "CHƯA THANH TOÁN");
        holder.status.setTextColor(holder.itemView.getContext().getColor(
                paid ? R.color.secondaryColor : R.color.errorColor));

        if (adminMode) {
            Employee employee = employeeDao.getById(salary.employeeId);
            holder.employeeName.setVisibility(View.VISIBLE);
            holder.employeeName.setText(employee == null
                    ? "Nhân viên #" + salary.employeeId
                    : employee.fullname);

            holder.markPaid.setVisibility(
                    !paid && listener != null ? View.VISIBLE : View.GONE);
            holder.markPaid.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMarkPaid(salary);
                }
            });
        } else {
            holder.employeeName.setVisibility(View.GONE);
            holder.markPaid.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return salaries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView employeeName;
        final TextView monthYear;
        final TextView status;
        final TextView totalHours;
        final TextView overtime;
        final TextView amount;
        final Button markPaid;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.tv_salary_employee_name);
            monthYear = itemView.findViewById(R.id.tv_salary_month_year);
            status = itemView.findViewById(R.id.tv_salary_status);
            totalHours = itemView.findViewById(R.id.tv_salary_total_hours);
            overtime = itemView.findViewById(R.id.tv_salary_overtime);
            amount = itemView.findViewById(R.id.tv_salary_total_amount);
            markPaid = itemView.findViewById(R.id.btn_mark_salary_paid);
        }
    }
}
