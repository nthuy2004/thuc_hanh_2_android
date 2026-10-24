package com.loader.thuc_hanh_2_android.ui.employee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/** Adapter hien thi Employee that (item_employee.xml cua P4). */
public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(Employee employee);

        void onDelete(Employee employee);
    }

    private final List<Employee> items = new ArrayList<>();
    private final Listener listener;

    public EmployeeListAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Employee> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee e = items.get(position);
        holder.tvName.setText(e.fullname);
        holder.tvCode.setText(holder.itemView.getContext()
                .getString(R.string.employee_item_code, e.code));
        holder.tvRate.setText(holder.itemView.getContext()
                .getString(R.string.employee_item_rate, FormatUtils.formatVND(e.hourlyRate)));
        holder.itemView.findViewById(R.id.btn_edit_employee)
                .setOnClickListener(v -> listener.onEdit(e));
        holder.itemView.findViewById(R.id.btn_delete_employee)
                .setOnClickListener(v -> listener.onDelete(e));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvCode;
        final TextView tvRate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvCode = itemView.findViewById(R.id.tv_employee_code);
            tvRate = itemView.findViewById(R.id.tv_employee_hourly_rate);
        }
    }
}
